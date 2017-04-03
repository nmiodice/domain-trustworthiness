package com.iodice.crawler.pagegraph;

import java.util.Set;

public interface PageGraph {

    String domainFromPageID(Integer id);

    void add(String sourceDomain, String destinationDomain);

    int size();

    Set<Integer> getPageIDs();

    Set<Integer> getOutboundLinks(Integer pageID);

    PageGraph collectAndRemoveDanglingPages(int iterationCount);

    void merge(PageGraph otherGraph);
}
