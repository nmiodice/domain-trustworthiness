package com.iodice.crawler.pagegraph;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MapDBPageGraph implements PageGraph {
    private DB db;
    private NavigableSet<Object[]> graph;
    private PageGraphUtil pageGraphUtil = new PageGraphUtil();

    MapDBPageGraph(DBType type) {
        switch (type) {
        case MEMORY:
            db = DBMaker.memoryDB()
                .make();
            break;
        case FILE:
            db = DBMaker.tempFileDB()
                .make();
            break;
        }

        graph = db.treeSetCreate("page-graph")
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
    public Set<Integer> getPageIDs() {
        return subsetUnion(new Object[] { Integer.MIN_VALUE }, new Object[] { Integer.MAX_VALUE });
    }

    @Override
    public Set<Integer> getOutboundLinks(Integer pageID) {
        return subsetUnion(new Object[] { pageID, Integer.MIN_VALUE }, new Object[] { pageID, Integer.MAX_VALUE });
    }

    private Set<Integer> subsetUnion(Object[] lo, Object[] hi) {
        return graph.subSet(lo, hi)
            .stream()
            .flatMap(Arrays::stream)
            .map(this::toInt)
            .collect(Collectors.toCollection(HashSet::new));
    }

    @SuppressWarnings("unchecked")
    private Integer toInt(Object o) {
        return (Integer) o;
    }

    @Override
    public void addReverseDanglingPageLinks() {
        for (Integer pageID : getDanglingPages()) {
            fixDanglingPage(pageID);
        }
    }

    private Set<Integer> getDanglingPages() {
        return getPageIDs().stream()
            .filter(x -> getOutboundLinks(x).isEmpty())
            .collect(Collectors.toCollection(HashSet::new));
    }

    private void fixDanglingPage(Integer danglingPageID) {
        Set<Integer> pointingToDangling = subsetUnion(new Object[] { Integer.MIN_VALUE, danglingPageID },
            new Object[] { Integer.MAX_VALUE, danglingPageID });

        pointingToDangling.remove(danglingPageID);

        for (Integer pointer : pointingToDangling) {
            add(danglingPageID, pointer);
        }
    }

    public enum DBType {
        MEMORY, FILE
    }
}
