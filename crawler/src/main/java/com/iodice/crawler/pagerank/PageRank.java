package com.iodice.crawler.pagerank;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ToString
public class PageRank {

    private Map<Integer, Double> map;

    PageRank() {
        this(new HashMap<>());
    }

    private PageRank(Map<Integer, Double> map) {
        this.map = new HashMap<>(map);
    }

    void setRank(Integer pageID, Double pageRank) {
        map.put(pageID, pageRank);
    }

    void addRank(Integer pageID, Double pageRank) {
        setRank(pageID, getRank(pageID) + pageRank);
    }

    public Double getRank(Integer pageID) {
        return map.getOrDefault(pageID, 0.0);
    }

    public Set<Integer> getPageIDs() {
        return map.keySet();
    }

    HashMap<Integer, Double> toMap() {
        return new HashMap<>(map);
    }

    /**
     * @param pageIDs a set of pages that should be now considered for page rank calculation, intialized with a default
     *                rank of 0
     */
    void foldInPageIDs(Set<Integer> pageIDs) {
        for (Integer pageID : pageIDs) {
            if (!map.containsKey(pageID)) {
                setRank(pageID, 0.0);
            }
        }
    }
}
