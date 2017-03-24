package com.iodice.crawler.pagegraph;

import java.util.Set;

public interface PageGraph {
    static PageGraph berkeleyBackedPageGraph() throws PageGraphException {
        return new BerkeleyDBPageGraph();
    }

    static PageGraph mapDBBackedPageGraph() {
        return new MapDBPageGraph();
    }

    static PageGraph hashMapBackedPageGraph() {
        return new HashMapPageGraph();
    }

    String domainFromPageID(Integer id);

    void add(String sourceDomain, String destinationDomain);

    int size();

    Set<Integer> getPageIDs();

    Set<Integer> getOutboundLinks(Integer pageID);

    void addReverseDanglingPageLinks();
}
