package com.iodice.crawler.scheduler.persistence;

import org.bson.Document;

import java.util.Collection;

public class PersistenceAdaptor {
    private static final String URL_GRAPH_COLLECTION = "UrlGraph";
    private static final String DOMAIN_GRAPH_COLLECTION = "DomainGraph";
    private static final String GRAPH_SOURCE_KEY = "source";
    private static final String GRAPH_DESTINATIONS_KEY = "destinations";
    private DBFacade db = new DBFacade();

    public boolean seenURL(String source) {
        return !db.get(new Document(GRAPH_SOURCE_KEY, source), URL_GRAPH_COLLECTION)
            .isEmpty();
    }

    public void storeURLEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(entry, URL_GRAPH_COLLECTION);
    }

    public void storeDomainEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(entry, DOMAIN_GRAPH_COLLECTION);
    }
}
