package com.iodice.crawler.scheduler.processor;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.sqs.simplequeue.QueueWriter;

public class WorkDispatchHandler implements ResponseHandler {
    private QueueWriter writer;

    WorkDispatchHandler() {
        writer = new QueueWriter(Config.getString("sqs.request.queue"));
    }

    @Override
    public WorkResponse handle(WorkResponse response) {
        StringBuilder sb = new StringBuilder();
        for (String url : response.getDestinations()) {
            sb.append(url).append("\n");
        }

        writer.send(sb.toString());
        return response;
    }
}
