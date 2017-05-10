package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * If the source has been seen, the whole response is filtered. Any destinations that have been seen will removed
 * from the returned response
 */
@AllArgsConstructor
class FilterSeenURLsHandler extends ValidatedResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(FilterSeenURLsHandler.class);

    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        if (seenSource(response)) {
            logger.warn(String.format("URL '%s' was seen before", response.getSource()));
            return null;
        }

        return response;
// TODO: is the following block needed? am I limiting which nodes are destinations only because they were a source
// TODO: before? seems wrong!

//
//        Collection<String> unvisitedDestinations = filterVisitedDestinations(response);
//        if (unvisitedDestinations.isEmpty()) {
//            return null;
//        } else {
//            return WorkResponse.builder()
//                .source(response.getSource())
//                .destinations(unvisitedDestinations)
//                .build();
//        }
    }

    private boolean seenSource(WorkResponse response) {
        String url = response.getSource();
        return persistence.isInEdgeGraph(Collections.singleton(url))
            .get(url);
    }

    private Collection<String> filterVisitedDestinations(WorkResponse response) {
        Collection<String> all = response.getDestinations();
        Map<String, Boolean> isSeen = persistence.isInEdgeGraph(all);
        Collection<String> unseen = response.getDestinations()
            .stream()
            .filter(url -> !isSeen.get(url))
            .collect(Collectors.toList());

        return unseen;
    }
}
