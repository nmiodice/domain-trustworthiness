package com.iodice.crawler.worker.frontier;

import com.iodice.config.Config;
import com.iodice.sqs.simplequeue.QueueException;
import com.iodice.sqs.simplequeue.QueueReader;
import com.iodice.sqs.simplequeue.QueueWriter;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WorkQueueAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(WorkQueueAdaptor.class);
    private static final String BATCH_PAYLOAD_KEY = "payload";

    private QueueReader incoming;
    private QueueWriter outgoing;

    private LinkedBlockingQueue<JSONObject> itemsToSend;

    WorkQueueAdaptor() {
        incoming = new QueueReader(Config.getString("sqs.request.queue"));
        outgoing = new QueueWriter(Config.getString("sqs.response.queue"));
        itemsToSend = new LinkedBlockingQueue<>(Config.getInt("sqs.response.cache_message_limit"));

        ExecutorService threadManager = Executors.newFixedThreadPool(1);
        threadManager.submit(new WorkQueueMessageBatchJob());
    }

    JSONObject dequeue() throws QueueException {
        String message = incoming.getMessage();
        return new JSONObject(message);
    }

    @SneakyThrows
    void enqueue(JSONObject item) {
        itemsToSend.offer(item, 60, TimeUnit.SECONDS);
    }

    class WorkQueueMessageBatchJob implements Runnable {
        // maximum SQS message length, imposed by Amazon
        private static final long MAX_LENGTH = 256 * 1024;
        // how many characters to allow for non-frontier item json content
        private static final long LENGTH_BUFFER = 4 * 1024;
        private static final long MAX_ITEMS_LENGTH = MAX_LENGTH - LENGTH_BUFFER;

        private boolean isRunning = true;
        private List<JSONObject> batched = new ArrayList<>();
        private long currentLength = 0;

        @Override
        public void run() {
            while (isRunning()) {
                try {
                    singleLoop();
                } catch (Exception e) {
                    logger.error("unexpected error! " + e.getMessage(), e);
                }
            }
        }

        private synchronized void singleLoop() {
            try {
                JSONObject item = itemsToSend.poll(10, TimeUnit.SECONDS);
                if (item == null) {
                    if (currentLength > 0) {
                        sendAndReset();
                    }
                } else {
                    addToBatchOrSend(item);
                }
            } catch (Exception e) {
                logger.error("failed to handle queue item response: " + e.getMessage(), e);
            }
        }

        private void addToBatchOrSend(JSONObject item) {
            long itemLength = item.toString().length();

            if (itemLength > MAX_ITEMS_LENGTH) {
                logger.warn("skipping item with length = " + itemLength);
                logger.debug(item.toString());
                return;
            }

            if (currentLength + itemLength < MAX_ITEMS_LENGTH) {
                addToBatch(item, itemLength);
            } else {
                sendAndReset();
                addToBatch(item, itemLength);
            }
        }

        private void addToBatch(JSONObject item, long itemLength) {
            batched.add(item);
            currentLength += itemLength;
        }

        private void sendAndReset() {
            logger.info("purging frontier with length = " + currentLength);
            JSONObject batchJSON = new JSONObject();
            batchJSON.put(BATCH_PAYLOAD_KEY, batched);

            try {
                outgoing.send(batchJSON.toString());
            } catch (Exception e) {
                logger.error("error encountered during send. dropping message", e);
            } finally {
                batched.clear();
                currentLength = 0;
            }
        }

        boolean isRunning() {
            return isRunning;
        }
    }
}
