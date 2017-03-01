package com.iodice.crawler.webcrawler;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PageGraph {

    private final ConcurrentMap<Integer, Set<Integer>> forwardMap;
    private final ConcurrentMap<Integer, Set<Integer>> reverseMap;
    private final AtomicInteger nextID;
    private final ConcurrentMap<String, Integer> domainToPageID;
    private final ConcurrentMap<Integer, String> pageIDtoDomain;

    public PageGraph() {
        forwardMap = new ConcurrentHashMap<>();
        reverseMap = new ConcurrentHashMap<>();
        nextID = new AtomicInteger(0);
        domainToPageID = new ConcurrentHashMap<>();
        pageIDtoDomain = new ConcurrentHashMap<>();
    }

    /**
     * for each of the dangling pages (a page that is pointed to, but points to no pages),
     * add a link from the dangling page to each of the pages that point to it
     */
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

    private Integer toPageID(String domain) {
        synchronized (domainToPageID) {
            if (!domainToPageID.containsKey(domain)) {
                Integer id = nextID.getAndIncrement();
                domainToPageID.put(domain, id);
                pageIDtoDomain.put(id, domain);
            }
            return domainToPageID.get(domain);
        }
    }

    public String domainFromPageID(Integer id) {
        return pageIDtoDomain.get(id);
    }

    public void add(String sourceDomain, String destinationDomain) {
        add(toPageID(sourceDomain), toPageID(destinationDomain));
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

    public int size() {
        return forwardMap.size();
    }

    public Set<Integer> getPageIDs() {
        return new HashSet<>(forwardMap.keySet());
    }

    public Set<Integer> getOutboundLinks(Integer pageID) {
        return new HashSet<>(forwardMap.get(pageID));
    }
}
