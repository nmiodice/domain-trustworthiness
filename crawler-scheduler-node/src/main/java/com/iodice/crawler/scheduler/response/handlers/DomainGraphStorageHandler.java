package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DomainGraphStorageHandler implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(DomainGraphStorageHandler.class);

    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse handle(WorkResponse response) {
        Validate.notNull(response);
        Validate.notNull(response.getSource());
        Validate.notNull(response.getDestinations());
        Validate.notEmpty(response.getDestinations());

        String sourceDomain = toDomain(response.getSource());
        if (sourceDomain == null) {
            logger.warn(String.format("url='%s' returned a null domain", response.getSource()));
            return response;
        }

        Collection<String> destinationDomains = response.getDestinations()
            .stream()
            .map(this::toDomain)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        persistence.storeDomainEdges(sourceDomain, destinationDomains);
        return response;
    }

    private String toDomain(String url) {
        try {
            return URL.parse(url)
                .host()
                .toString();
        } catch (Exception ignored) {
            return null;
        }
    }
}
