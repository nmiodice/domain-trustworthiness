package com.iodice.crawler.worker.queue;

public class QueueWriter extends QueueBase {
    QueueWriter(String queueURL) {
        super(queueURL);
    }

    void send(String message) {
        sqsClient.sendMessage(queueUrl, message);
    }
}
