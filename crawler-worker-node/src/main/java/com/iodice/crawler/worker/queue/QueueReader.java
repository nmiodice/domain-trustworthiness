package com.iodice.crawler.worker.queue;

import com.amazonaws.services.sqs.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class QueueReader extends QueueBase {
    private static final Logger logger = LoggerFactory.getLogger(QueueReader.class);

    QueueReader(String queueURL) {
        super(queueURL);
    }

    /**
     * A blocking operation which waits for the next message and returns the job parameters parsed from that message
     */
    String getMessage() throws QueueException {
        Message queueMessage = getMessageBlocking();
        sqsClient.deleteMessage(queueUrl, queueMessage.getReceiptHandle());
        return queueMessage.getBody();
    }

    private Message getMessageBlocking() {
        List<Message> messages;
        do {
            logger.info("waiting for messages on queue (" + queueUrl + ")");
            messages = sqsClient.receiveMessage(queueUrl)
                .getMessages();
            logger.debug("got " + messages.size() + " messages on queue (" + queueUrl + ")");
        } while (messages.isEmpty());

        return messages.get(0);
    }
}
