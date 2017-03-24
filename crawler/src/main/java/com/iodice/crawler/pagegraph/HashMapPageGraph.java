package com.iodice.crawler.pagegraph;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashMapPageGraph implements PageGraph {
    private PageGraphUtil pageGraphUtil;
    private Map<Integer, Node> nodes;

    HashMapPageGraph() {
        pageGraphUtil = new PageGraphUtil();
        nodes = new HashMap<>();
    }

    @Override
    public String domainFromPageID(Integer id) {
        return pageGraphUtil.domain(id);
    }

    @Override
    public void add(String sourceDomain, String destinationDomain) {
        Integer srcID = pageGraphUtil.toPageID(sourceDomain);
        Integer dstID = pageGraphUtil.toPageID(destinationDomain);

        nodes.putIfAbsent(srcID, new Node());
        nodes.get(srcID).getOutgoing().add(dstID);

        nodes.putIfAbsent(dstID, new Node());
        nodes.get(dstID).getIncoming().add(srcID);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Set<Integer> getPageIDs() {
        return nodes.keySet();
    }

    @Override
    public Set<Integer> getOutboundLinks(Integer pageID) {
        return nodes.containsKey(pageID) ? nodes.get(pageID).getIncoming() : Collections.emptySet();
    }

    @Override
    public void addReverseDanglingPageLinks() {
        for (Integer pageID : nodes.keySet()) {
            if (nodes.get(pageID).getOutgoing().isEmpty()) {
                nodes.get(pageID).getOutgoing().addAll(nodes.get(pageID).getIncoming());
            }
        }
    }

    private class Node {
        @Getter
        private Set<Integer> incoming = new HashSet<>();
        @Getter
        private Set<Integer> outgoing = new HashSet<>();
    }
}
