package com.iodice.crawler.worker.queue;

import com.iodice.config.Config;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.Validate;
import org.json.simple.JSONObject;

import java.util.Arrays;
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
            return Arrays.stream(message.split("\n"))
                .filter(line -> line.length() > 0)
                .map(WebURL::new)
                .collect(Collectors.toList());
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void scheduleAll(Collection<WebURL> destinations, WebURL source) {
        Validate.notNull(destinations, "cannot schedule null URL list");
        Validate.notNull(source, "cannot schedule without a source");
        Validate.notEmpty(source.getUrl(), "cannot schedule with empty source URL");

        if (destinations.size() == 0) {
            return;
        }

        JSONObject message = new JSONObject();
        message.put(SOURCE_KEY, source.getUrl());

        List<String> destinationUrls = destinations.stream()
            .map(WebURL::getUrl)
            .collect(Collectors.toList());
        message.put(DESTINATION_KEY, destinationUrls);
        outgoing.send(message.toJSONString());
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
