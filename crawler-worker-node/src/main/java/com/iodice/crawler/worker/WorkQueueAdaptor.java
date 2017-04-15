package com.iodice.crawler.worker;

import com.iodice.config.Config;
import com.iodice.crawler.worker.queue.QueueException;
import com.iodice.crawler.worker.queue.QueueReader;
import com.iodice.crawler.worker.queue.QueueWriter;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorkQueueAdaptor implements Frontier {
    private QueueReader incoming;
    private QueueWriter outging;

    public WorkQueueAdaptor() {
        incoming = new QueueReader(Config.getString("sqs.request.queue"));
        outging = new QueueWriter(Config.getString("sqs.response.queue"));
    }

    @Override
    public List<WebURL> getNextURLs() {
        try {
            String message = incoming.getMessage();
            return Arrays.stream(message.split("\n"))
                .map(url -> WebURL.builder()
                    .url(url)
                    .build())
                .collect(Collectors.toList());
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void scheduleAll(Collection<WebURL> destinations, WebURL source) {
        System.out.println("hahaha " + source.getUrl());
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
