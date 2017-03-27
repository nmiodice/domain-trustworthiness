package com.iodice.crawler.pagegraph;

import com.iodice.crawler.persistence.PersistentMultiMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BerkeleyDBPageGraph extends BasePageGraph {
    private final PersistentMultiMap forwardLinkMap;
    private final PersistentMultiMap reverseLinkMap;

    BerkeleyDBPageGraph() throws PageGraphException {
        try {
            forwardLinkMap = new PersistentMultiMap();
            reverseLinkMap = new PersistentMultiMap();
            pageGraphUtil = new PageGraphUtil();
        } catch (Exception e) {
            throw new PageGraphException("error initializing graph: " + e.getMessage(), e);
        }
    }

    @Override
    public void add(String sourceDomain, String destinationDomain) {
        add(pageGraphUtil.toPageID(sourceDomain.toLowerCase()),
            pageGraphUtil.toPageID(destinationDomain.toLowerCase()));
    }

    private void add(Integer sourcePageID, Integer destinationPageID) {
        addLink(sourcePageID, destinationPageID, forwardLinkMap);
        addLink(destinationPageID, sourcePageID, reverseLinkMap);
    }

    private void addLink(Integer source, Integer destination, PersistentMultiMap container) {
        container.put(source, destination);
    }

    @Override
    public int size() {
        return forwardLinkMap.size();
    }

    @Override
    public Set<Integer> getPageIDs() {
        return new HashSet<>(forwardLinkMap.keys());
    }

    @Override
    public Set<Integer> getOutboundLinks(Integer pageID) {
        return new HashSet<>(forwardLinkMap.get(pageID));
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

        for (Integer pageID : forwardLinkMap.keys()) {
            if (forwardLinkMap.get(pageID).isEmpty()) {
                for (Integer pointingToDanglingPage : reverseLinkMap.get(pageID)) {
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
