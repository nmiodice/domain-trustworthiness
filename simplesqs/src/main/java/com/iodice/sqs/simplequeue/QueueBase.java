package com.iodice.sqs.simplequeue;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

class QueueBase {
    final AmazonSQS sqsClient;
    final String queueUrl;

    QueueBase(String queueURL) {
        sqsClient = new AmazonSQSClient(new ProfileCredentialsProvider().getCredentials());
        this.queueUrl = queueURL;
    }
}
