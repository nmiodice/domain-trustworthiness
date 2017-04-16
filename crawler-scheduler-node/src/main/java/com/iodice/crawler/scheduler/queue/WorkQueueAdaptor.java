package com.iodice.crawler.scheduler.queue;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.sqs.simplequeue.QueueException;
import com.iodice.sqs.simplequeue.QueueReader;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class WorkQueueAdaptor {
    private static final String SOURCE_KEY = "source";
    private static final String DESTINATION_KEY = "destination";

    private QueueReader reader;

    public WorkQueueAdaptor() {
        reader = new QueueReader(Config.getString("sqs.response.queue"));
    }

    public WorkResponse nextResponse() throws QueueException {
        String message = reader.getMessage();
        try {
            JSONObject responseJSON = new JSONObject(message);
            return jsonToWorkerResponse(responseJSON);
        } catch (Exception e) {
            throw new QueueException("can not parse worker response", e);
        }
    }

    private WorkResponse jsonToWorkerResponse(JSONObject json) throws Exception {
        Collection<String> destinations = json.getJSONArray(DESTINATION_KEY)
            .toList()
            .stream()
            .map(Object::toString)
            .collect(Collectors.toCollection(HashSet::new));

        return WorkResponse.builder()
            .source(json.getString(SOURCE_KEY))
            .destinations(destinations)
            .build();
    }

}
