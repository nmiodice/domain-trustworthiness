package com.iodice.crawler.worker.frontier;

import com.iodice.sqs.simplequeue.QueueException;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.Validate;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FrontierAdaptor implements Frontier {
    private static final String SOURCE_KEY = "source";
    private static final String DESTINATION_KEY = "destination";
    private WorkQueueAdaptor queueAdaptor;

    public FrontierAdaptor() {
        queueAdaptor = new WorkQueueAdaptor();
    }

    @Override
    public List<WebURL> getNextURLs() {
        try {
            JSONObject json = queueAdaptor.dequeue();
            return json.getJSONArray("urls")
                .toList()
                .stream()
                .map(Object::toString)
                .map(WebURL::new)
                .collect(Collectors.toList());
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void scheduleAll(Collection<WebURL> destinations, WebURL source) {
        Validate.notNull(destinations, "cannot schedule null URL list");
        Validate.notNull(source, "cannot schedule without a source");
        Validate.notEmpty(source.getUrl(), "cannot schedule with empty source URL");

        if (destinations.size() == 0) {
            return;
        }

        JSONObject json = new JSONObject();
        json.put(SOURCE_KEY, source.getUrl());

        List<String> destinationUrls = destinations.stream()
            .map(WebURL::getUrl)
            .collect(Collectors.toList());
        json.put(DESTINATION_KEY, destinationUrls);
        queueAdaptor.enqueue(json);
    }

    @Override
    public void schedule(WebURL destination, WebURL source) {
        scheduleAll(Collections.singletonList(destination), source);
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public void shutdown() {

    }
}
