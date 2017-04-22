package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FilterSeenHandler extends ValidatedHandler {
    private static final Logger logger = LoggerFactory.getLogger(FilterSeenHandler.class);

    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        if (persistence.seenURL(response.getSource())) {
            logger.warn(String.format("URL '%s' was seen before", response.getSource()));
            return null;
        }

        Collection<String> all = response.getDestinations();
        Map<String, Boolean> isSeen = persistence.seenURLS(all);
        Collection<String> unseen = response.getDestinations()
            .stream()
            .filter(url -> !isSeen.get(url))
            .collect(Collectors.toList());

        int filteredCount = all.size() - unseen.size();
        logger.info(String.format("filtered %d destinations from '%s'", filteredCount, response.getSource()));

        return WorkResponse.builder()
            .source(response.getSource())
            .destinations(unseen)
            .build();
    }
}
