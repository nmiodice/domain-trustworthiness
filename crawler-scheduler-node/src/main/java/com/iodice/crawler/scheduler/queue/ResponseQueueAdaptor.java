package com.iodice.crawler.scheduler.queue;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.sqs.simplequeue.QueueException;
import com.iodice.sqs.simplequeue.QueueReader;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseQueueAdaptor {
    private static final String SOURCE_KEY = "source";
    private static final String DESTINATION_KEY = "destination";
    private static final String BATCH_PAYLOAD_KEY = "payload";

    private QueueReader reader;

    public ResponseQueueAdaptor() {
        reader = new QueueReader(Config.getString("sqs.response.queue"));
    }

    public List<WorkResponse> nextResponseBatch() throws QueueException {
        try {
            List<WorkResponse> responses = new ArrayList<>();
            JSONObject responseJSON = new JSONObject(reader.getMessage());
            JSONArray responseObjects = responseJSON.getJSONArray(BATCH_PAYLOAD_KEY);

            for (int i = 0; i < responseObjects.length(); i++) {
                responses.add(jsonToWorkerResponse(responseObjects.getJSONObject(i)));
            }

            return responses;
        } catch (Exception e) {
            throw new QueueException("can not parse worker response", e);
        }
    }

    @SneakyThrows
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
