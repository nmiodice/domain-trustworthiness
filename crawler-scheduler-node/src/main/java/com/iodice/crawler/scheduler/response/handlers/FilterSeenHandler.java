package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

public class FilterSeenHandler implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(FilterSeenHandler.class);

    private PersistenceAdaptor persistence = new PersistenceAdaptor();

    @Override
    public WorkResponse handle(WorkResponse response) {
        if (persistence.seenURL(response.getSource())) {
            logger.warn(String.format("URL '%s' was seen before", response.getSource()));
            return null;
        }
        Collection<String> all = response.getDestinations();
        Collection<String> unseen = all.stream()
            .filter(url -> !persistence.seenURL(url))
            .collect(Collectors.toList());

        int filteredCount = all.size() - unseen.size();
        logger.info(String.format("filtered %d destinations from '%s'", filteredCount, response.getSource()));

        return WorkResponse.builder()
            .source(response.getSource())
            .destinations(unseen)
            .build();
    }
}
