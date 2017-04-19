package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.queue.ResponseQueueAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

class ResponseWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ResponseWorker.class);
    private final static AtomicInteger WORKER_ID_COUNTER = new AtomicInteger();

    private ResponseQueueAdaptor responseQueue;
    private ResponseHandlerPipeline responseHandler;
    private boolean stopped = false;
    private final int threadID;

    ResponseWorker() {
        responseQueue = new ResponseQueueAdaptor();
        responseHandler = ResponseHandlerPipelineFactory.defaultPipeline();
        threadID = WORKER_ID_COUNTER.incrementAndGet();
    }

    public void stop() {
        logger.info(String.format("worker %d is stopping", threadID));
        stopped = true;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                WorkResponse response = responseQueue.nextResponse();
                responseHandler.handle(response);
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error(String.format("worker %d encountered an exception: %s", threadID, e.getMessage()), e);
            }
        }
    }
}
