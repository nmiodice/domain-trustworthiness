package com.iodice.crawler.pagerank;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ToString
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

    HashMap<Integer, Double> toMap() {
        return new HashMap<>(map);
    }

    public int size() {
        return map.size();
    }
}
