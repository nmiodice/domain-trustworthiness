package com.iodice.crawler.pagegraph;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Wraps an existing {@link PageGraph} with a cache. This is a read only view on the page graph, and assumes that the
 * graph being cached is not going to change while this cache is being used. That means that any modification to the
 * underlying graph may not be represented in the cached values returned by this graph.
 */
public class CachedPageGraph implements PageGraph {

    private PageGraph graph;
    private int graphSize;
    private Set<Integer> pageIDs;
    private LoadingCache<Integer, Set<Integer>> graphCache;

    CachedPageGraph(PageGraph graph) {
        this.graph = graph;
        this.graphSize = graph.size();
        this.pageIDs = graph.getPageIDs();

        graphCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, Set<Integer>>() {
                public Set<Integer> load(Integer pageID) {
                    return graph.getOutboundLinks(pageID);
                }
            });
    }

    @Override
    public String domainFromPageID(Integer id) {
        return graph.domainFromPageID(id);
    }

    @Override
    public void add(String sourceDomain, String destinationDomain) {
        throw new UnsupportedOperationException("this cache is read only!");
    }

    @Override
    public int size() {
        return graphSize;
    }

    @Override
    public Set<Integer> getPageIDs() {
        return pageIDs;
    }

    @Override
    @SneakyThrows
    public Set<Integer> getOutboundLinks(Integer pageID) {
        return graphCache.get(pageID);
    }

    @Override
    public void addReverseDanglingPageLinks() {
        graph.addReverseDanglingPageLinks();
    }
}
