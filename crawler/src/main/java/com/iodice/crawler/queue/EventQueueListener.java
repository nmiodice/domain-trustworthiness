package com.iodice.crawler.queue;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.iodice.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(EventQueueListener.class);

    private AmazonSQS sqsClient;
    private String queueURL;

    public EventQueueListener() {
        sqsClient = new AmazonSQSClient(new ProfileCredentialsProvider().getCredentials());
        queueURL = Config.getString("sqs.queue_url");
    }

    /**
     * A blocking operation which waits for the next message and returns the job parameters parsed from that message
     */
    public PageRankJobParams getNextJobParameters() throws EventListenerException {
        Message queueMessage = getMessageBlocking();
        sqsClient.deleteMessage(queueURL, queueMessage.getReceiptHandle());
        try {
            return PageRankJobParams.builder()
                .runtime(Integer.parseInt(queueMessage.getBody()))
                .build();
        } catch (NumberFormatException e) {
            throw new EventListenerException("bad message format: " + queueMessage.getBody(), e);
        }
    }

    private Message getMessageBlocking() {
        List<Message> messages;
        do {
            logger.info("waiting for messages on queue (" + queueURL + ")");
            messages = sqsClient.receiveMessage(queueURL)
                .getMessages();
            logger.debug("got " + messages.size() + " messages on queue (" + queueURL + ")");
        } while (messages.isEmpty());

        return messages.get(0);
    }
}
