package com.iodice.crawler.scheduler.queue;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.sqs.simplequeue.QueueWriter;
import org.json.JSONObject;

public class RequestQueueAdaptor {
    private static final String URLS_KEY = "urls";

    private QueueWriter writer;

    public RequestQueueAdaptor() {
        writer = new QueueWriter(Config.getString("sqs.request.queue"));
    }

    public void emitRequest(WorkRequest request) {
        JSONObject message = new JSONObject();
        message.put(URLS_KEY, request.getUrls());
        writer.send(message.toString());
    }
}
