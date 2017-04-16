package com.iodice.sqs.simplequeue;

public class QueueWriter extends QueueBase {
    public QueueWriter(String queueURL) {
        super(queueURL);
    }

    public void send(String message) {
        sqsClient.sendMessage(queueUrl, message);
    }
}
