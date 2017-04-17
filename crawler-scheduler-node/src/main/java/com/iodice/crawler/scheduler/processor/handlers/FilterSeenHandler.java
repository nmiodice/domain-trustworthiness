package com.iodice.crawler.scheduler.processor.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;

import java.util.List;
import java.util.stream.Collectors;

public class FilterSeenHandler implements ResponseHandler {
    private PersistenceAdaptor persistence = new PersistenceAdaptor();

    @Override
    public WorkResponse handle(WorkResponse response) {
        List<String> unseen = response.getDestinations()
            .stream()
            .filter(url -> !persistence.seenURL(url))
            .collect(Collectors.toList());
        return WorkResponse.builder()
            .source(response.getSource())
            .destinations(unseen)
            .build();
    }
}
