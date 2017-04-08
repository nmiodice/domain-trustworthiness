package com.iodice.crawler.pagegraph;

import java.util.Set;

public interface PageGraph {

    String toDomain(Integer id);

    void add(String sourceDomain, String destinationDomain);

    int size();

    int size(int pageID);

    Set<Integer> getPageIDs();

    Set<Integer> getOutboundLinks(Integer pageID);

    Set<String> getOutboundLinks(String domain);

    PageGraph pruneDanglingPages(int iterationCount);

    Set<Integer> merge(PageGraph otherGraph);

    void close();
}
