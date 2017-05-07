package com.iodice.crawler.scheduler.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PersistenceAdaptor {
    void storeDomainEdges(String source, Collection<String> destinations);

    void storeURLEdges(String source, Collection<String> destinations);

    Map<String, Boolean> isInEdgeGraph(Collection<String> urls);

    void enqueueURLs(Collection<String> urls);

    List<String> dequeueURLs(int count);

    int getDomainScheduledCount(String domain);
}
