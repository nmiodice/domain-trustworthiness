package com.iodice.crawler.persistence;

import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagerank.PageRank;
import com.iodice.persistence.PageRankStore;

import java.util.HashMap;
import java.util.Map;

public class PageRankStoreAdaptor {
    private final PageRankStore store = new PageRankStore();

    public void store(PageRank pageRank, PageGraph pageGraph) {
        Map<String, Double> domainRanks = new HashMap<>();
        for (Integer pageID : pageRank.getPageIDs()) {
            domainRanks.put(pageGraph.toDomain(pageID), pageRank.getRank(pageID));
        }
        store.store(domainRanks);
    }

    public void deleteAll() {
        store.deleteAll();
    }
}
