package com.iodice.crawler.pagegraph;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapPageGraph implements PageGraph {
    private final Map<Integer, Set<Integer>> forwardMap;
    private final Map<Integer, Set<Integer>> reverseMap;
    private PageGraphUtil pageGraphUtil;

    HashMapPageGraph() {
        forwardMap = new ConcurrentHashMap<>();
        reverseMap = new ConcurrentHashMap<>();
        pageGraphUtil = new PageGraphUtil();
    }

    public String domainFromPageID(Integer id) {
        return pageGraphUtil.domain(id);
    }

    public void add(String sourceDomain, String destinationDomain) {
        add(pageGraphUtil.toPageID(sourceDomain.toLowerCase()),
            pageGraphUtil.toPageID(destinationDomain.toLowerCase()));
    }

    private void add(Integer sourcePageID, Integer destinationPageID) {
        addLink(sourcePageID, destinationPageID, forwardMap);
        addLink(destinationPageID, sourcePageID, reverseMap);
    }

    private void addLink(Integer source, Integer destination, Map<Integer, Set<Integer>> container) {
        container.putIfAbsent(source, Sets.newConcurrentHashSet());
        container.get(source).add(destination);
        container.putIfAbsent(destination, Sets.newConcurrentHashSet());
    }

    @Override
    public int size() {
        return forwardMap.size();
    }

    @Override
    public Set<Integer> getPageIDs() {
        return new HashSet<>(forwardMap.keySet());
    }

    @Override
    public Set<Integer> getOutboundLinks(Integer pageID) {
        return new HashSet<>(forwardMap.get(pageID));
    }

    /**
     * for each of the dangling pages (a page that is pointed to, but points to no pages),
     * add a link from the dangling page to each of the pages that point to it
     */
    @Override
    public void addReverseDanglingPageLinks() {
        // collect into a new map, then use an API call to add the data so that the forward and reverse
        // maps remain consistent with each other
        Map<Integer, Set<Integer>> toAdd = new HashMap<>();

        for (Integer pageID : forwardMap.keySet()) {
            if (forwardMap.get(pageID).isEmpty()) {
                for (Integer pointingToDanglingPage : reverseMap.get(pageID)) {
                    toAdd.putIfAbsent(pageID, new HashSet<>());
                    toAdd.get(pageID).add(pointingToDanglingPage);
                }
            }
        }

        for (Integer sourceID : toAdd.keySet()) {
            for (Integer destinationID : toAdd.get(sourceID)) {
                add(sourceID, destinationID);
            }
        }
    }
}
