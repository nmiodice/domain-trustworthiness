package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.utils.URLFacade;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * stores a graph with nodes like:
 * domain -> [domain]
 */
@AllArgsConstructor
class DomainGraphStorageHandler extends ValidatedResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(DomainGraphStorageHandler.class);

    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        String sourceDomain = URLFacade.toDomain(response.getSource());
        if (sourceDomain == null) {
            logger.warn(String.format("url='%s' returned a null domain", response.getSource()));
            return response;
        }

        Collection<String> destinationDomains = response.getDestinations()
            .stream()
            .map(URLFacade::toDomain)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        persistence.storeDomainEdges(sourceDomain, destinationDomains);
        return response;
    }
}
