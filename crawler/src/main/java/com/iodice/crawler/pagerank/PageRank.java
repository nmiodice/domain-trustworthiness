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
     * @param pageIDs a set of pages that should be now considered for page rank calculation, initialized with a default
     *                rank of 0 UNLESS the existing graph is empty, in which case the new nodes should be initialized
     *                with a rank = 1 / (# new pages). This is only an issue for very small graphs
     */
    void foldInPageIDs(Set<Integer> pageIDs) {
        double toAdd = 0.0;
        if (getPageIDs().size() == 0) {
            toAdd = 1.0 / (double) pageIDs.size();
        }

        for (Integer pageID : pageIDs) {
            addRank(pageID, toAdd);
        }
    }

    void add(PageRank otherRank) {
        for (Integer pageID : otherRank.getPageIDs()) {
            addRank(pageID, otherRank.getRank(pageID));
        }
    }
}
