package com.iodice.crawler.pagegraph;

import org.apache.commons.lang.Validate;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapDBPageGraph implements PageGraph {
    private static final DB memoryDB = DBMaker.memoryDB()
        .make();
    private static final DB fileDB = DBMaker.tempFileDB()
        .make();

    private NavigableSet<Object[]> graph;
    private PageGraphUtil pageGraphUtil = new PageGraphUtil();

    MapDBPageGraph(DBType type) {
        DB db = DBType.MEMORY.equals(type) ? memoryDB : fileDB;
        graph = db.treeSetCreate(UUID.randomUUID()
            .toString())
            .serializer(BTreeKeySerializer.ARRAY2)
            .make();
    }

    @Override
    public String domainFromPageID(Integer id) {
        return pageGraphUtil.domain(id);
    }

    @Override
    public void add(String sourceDomain, String destinationDomain) {
        add(pageGraphUtil.toPageID(sourceDomain.toLowerCase()),
            pageGraphUtil.toPageID(destinationDomain.toLowerCase()));
    }

    private void add(Integer sourceID, Integer destinationID) {
        graph.add(new Integer[] { sourceID, destinationID });
    }

    @Override
    public int size() {
        return getPageIDs().size();
    }

    @Override
    public int size(int pageID) {
        return getOutboundLinks(pageID).size();
    }

    @Override
    public Set<Integer> getPageIDs() {
        Object[] lo = new Object[] { Integer.MIN_VALUE };
        Object[] hi = new Object[] { Integer.MAX_VALUE };

        return graph.subSet(lo, hi)
            .stream()
            .flatMap(Arrays::stream)
            .map(this::toInt)
            .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Integer> getOutboundLinks(Integer pageID) {
        Object[] lo = new Object[] { pageID, Integer.MIN_VALUE };
        Object[] hi = new Object[] { pageID, Integer.MAX_VALUE };
        return graph.subSet(lo, hi)
            .stream()
            .map(x -> toInt(x[1]))
            .collect(Collectors.toCollection(HashSet::new));

    }

    @SuppressWarnings("unchecked")
    private Integer toInt(Object o) {
        return (Integer) o;
    }

    @Override
    public PageGraph collectAndRemoveDanglingPages(int iterationCount) {
        Validate.isTrue(iterationCount > 0);
        MapDBPageGraph allDanglers = new MapDBPageGraph(DBType.FILE);

        // the algorithm here is to remove all dangling links during each iteration. by removing dangling links, it
        // is typical that there are now more dangling links that exist. so we must do this for a number of
        // iterations. It is possible that after all iterations, there still exist dangling links
        for (int i = 0; i < iterationCount; i++) {
            Set<Integer[]> danglingEntries = getDanglingPages().parallelStream()
                .map(this::getPointersToPage)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashSet::new));

            for (Integer[] entry : danglingEntries) {
                // add to the dangling graph as a domain, so they get proper domain <-> pageID mappings
                allDanglers.add(domainFromPageID(entry[0]), domainFromPageID(entry[1]));
                // remove from this graph
                graph.remove(entry);
            }
        }
        return allDanglers;
    }

    private Set<Integer> getDanglingPages() {
        return getPageIDs().stream()
            .filter(x -> getOutboundLinks(x).isEmpty())
            .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<Integer[]> getPointersToPage(Integer pageID) {
        return getPageIDs().parallelStream()
            .filter(aPage -> this.getOutboundLinks(aPage)
                .contains(pageID))
            .map(aPage -> new Integer[] { aPage, pageID })
            .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Integer> merge(PageGraph otherGraph) {
        Set<Integer> preMergeIDs = getPageIDs();
        for (Integer pageID : otherGraph.getPageIDs()) {
            Set<String[]> toAdd = otherGraph.getOutboundLinks(pageID)
                .stream()
                .map(outboundID -> new String[] { otherGraph.domainFromPageID(pageID),
                    otherGraph.domainFromPageID(outboundID) })
                .collect(Collectors.toCollection(HashSet::new));

            for (String[] entry : toAdd) {
                add(entry[0], entry[1]);
            }
        }

        Set<Integer> postMergeIDs = getPageIDs();
        postMergeIDs.removeAll(preMergeIDs);
        return postMergeIDs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass()
            .getSimpleName())
            .append("(graph={");
        for (Integer src : getPageIDs()) {
            for (Integer dst : getOutboundLinks(src)) {
                sb.append(String.format("[%d->%d], ", src, dst));
            }
        }
        return sb.toString()
            .trim()
            .substring(0, sb.length() - 2) + "})";
    }

    public enum DBType {
        MEMORY, FILE
    }
}
