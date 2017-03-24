package com.iodice.crawler.pagegraph;

import java.util.Set;

public interface PageGraph {
    static PageGraph berkelyBackedPageGraph() throws PageGraphException {
        return new BerkeleyDBPageGraph();
    }

    void addReverseDanglingPageLinks();

    String domainFromPageID(Integer id);

    void add(String sourceDomain, String destinationDomain);

    int size();

    Set<Integer> getPageIDs();

    Set<Integer> getOutboundLinks(Integer pageID);
}
