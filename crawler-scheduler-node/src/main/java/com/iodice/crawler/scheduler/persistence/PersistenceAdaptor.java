package com.iodice.crawler.scheduler.persistence;

import org.bson.Document;

import java.util.Collection;

public class PersistenceAdaptor {
    private static final String GRAPH_COLLECTION = "PageGraph";
    private static final String GRAPH_SOURCE_KEY = "source";
    private static final String GRAPH_DESTINATIONS_KEY = "destinations";
    private DBFacade db = new DBFacade();

    public boolean seenURL(String source) {
        return !db.get(new Document(GRAPH_SOURCE_KEY, source), GRAPH_COLLECTION)
            .isEmpty();
    }

    public void storeLinks(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(entry, GRAPH_COLLECTION);
    }
}
