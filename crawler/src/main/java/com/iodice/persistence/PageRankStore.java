package com.iodice.persistence;

import com.iodice.crawler.PageGraph;
import com.iodice.pagerank.PageRank;

import java.util.HashMap;
import java.util.Map;

public class PageRankStore {
    private PageRankStoreAdaptor adaptor = new PageRankStoreAdaptor();

    public void store(PageRank pageRank, PageGraph pageGraph) {
        Map<String, Double> domainRanks = new HashMap<>();
        for (Integer pageID : pageRank.getPageIDs()) {
            domainRanks.put(pageGraph.domainFromPageID(pageID), pageRank.getRank(pageID));
        }
        adaptor.store(domainRanks);
    }

    public void deleteAll() {
        adaptor.deleteAll();
    }
}
