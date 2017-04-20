package com.iodice.crawler.worker.queue;

import com.iodice.config.Config;
import com.iodice.sqs.simplequeue.QueueException;
import com.iodice.sqs.simplequeue.QueueReader;
import com.iodice.sqs.simplequeue.QueueWriter;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.Validate;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorkQueueAdaptor implements Frontier {
    private static final String SOURCE_KEY = "source";
    private static final String DESTINATION_KEY = "destination";
    private QueueReader incoming;
    private QueueWriter outgoing;

    public WorkQueueAdaptor() {
        incoming = new QueueReader(Config.getString("sqs.request.queue"));
        outgoing = new QueueWriter(Config.getString("sqs.response.queue"));
    }

    @Override
    public List<WebURL> getNextURLs() {
        try {
            String message = incoming.getMessage();
            JSONObject json = new JSONObject(message);
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
        outgoing.send(json.toString());
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
