package com.iodice.crawler.worker.queue;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class QueueBase {
    final AmazonSQS sqsClient;
    final String queueUrl;

    public QueueBase(String queueURL) {
        sqsClient = new AmazonSQSClient(new ProfileCredentialsProvider().getCredentials());
        this.queueUrl = queueURL;
    }
}
