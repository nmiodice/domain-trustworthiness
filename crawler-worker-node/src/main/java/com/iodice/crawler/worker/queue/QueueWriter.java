package com.iodice.crawler.worker.queue;


public class QueueWriter extends QueueBase {
    public QueueWriter(String queueURL) {
        super(queueURL);
    }

    public void send(String message) {
        System.out.println("-----" + queueUrl);
        sqsClient.sendMessage(queueUrl, message);
    }
}
