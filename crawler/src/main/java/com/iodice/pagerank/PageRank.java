package com.iodice.pagerank;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageRank {

    private Map<Integer, Double> map = new HashMap<>();

    void setRank(Integer pageID, Double pageRank) {
        map.put(pageID, pageRank);
    }

    public Double getRank(Integer pageID) {
        return getRankWithDefault(pageID, 0.0);
    }

    Double getRankWithDefault(Integer pageID, Double defaultValue) {
        return map.getOrDefault(pageID, defaultValue);
    }

    public Set<Integer> getPageIDs() {
        return map.keySet();
    }

    HashMap<Integer, Double> toSet() {
        return new HashMap<>(map);
    }
}
