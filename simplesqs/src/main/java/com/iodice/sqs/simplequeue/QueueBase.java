package com.iodice.sqs.simplequeue;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


class QueueBase {
    final AmazonSQS sqsClient;
    final String queueUrl;

    QueueBase(String queueURL) {
        System.out.println(SDKGlobalConfiguration.class.getProtectionDomain().getCodeSource().getLocation());
        sqsClient = AmazonSQSClientBuilder.defaultClient();
        this.queueUrl = queueURL;
    }
}
