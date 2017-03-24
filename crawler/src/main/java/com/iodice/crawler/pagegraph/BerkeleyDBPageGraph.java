package com.iodice.crawler.pagegraph;

import com.iodice.crawler.persistence.PersistentMultiMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BerkeleyDBPageGraph implements PageGraph {
    private final DB db = DBMaker.tempFileDB().make();

    private final AtomicInteger nextID;

    private final PersistentMultiMap forwardLinkMap;
    private final PersistentMultiMap reverseLinkMap;

    private final Map<String, Integer> domainToPageID;
    private final Map<Integer, String> pageIDtoDomain;

    BerkeleyDBPageGraph() throws PageGraphException {
        nextID = new AtomicInteger(0);

        try {
            forwardLinkMap = new PersistentMultiMap();
            reverseLinkMap = new PersistentMultiMap();
        } catch (Exception e) {
            throw new PageGraphException("error initializing graph: " + e.getMessage(), e);
        }

        domainToPageID = db.hashMap("domain-to-page-id", Serializer.STRING, Serializer.INTEGER).createOrOpen();
        pageIDtoDomain = db.hashMap("page-id-to-domain", Serializer.INTEGER, Serializer.STRING).createOrOpen();
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

    @Override
    public String domainFromPageID(Integer id) {
        return pageIDtoDomain.get(id);
    }

    @Override
    public void add(String sourceDomain, String destinationDomain) {
        add(toPageID(sourceDomain.toLowerCase()), toPageID(destinationDomain.toLowerCase()));
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
}
