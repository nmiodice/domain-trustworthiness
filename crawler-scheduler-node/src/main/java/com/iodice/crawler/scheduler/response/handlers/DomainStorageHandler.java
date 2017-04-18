package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class DomainStorageHandler implements ResponseHandler {
    private PersistenceAdaptor persistence = new PersistenceAdaptor();

    @Override
    public WorkResponse handle(WorkResponse response) {
        String sourceDomain = toDomain(response.getSource());
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
            return URL.parse(url).host().toString();
        } catch (GalimatiasParseException ex) {
            return null;
        }
    }
}
