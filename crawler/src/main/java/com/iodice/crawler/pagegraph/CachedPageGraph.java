package com.iodice.crawler.pagegraph;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;

import java.util.Set;

/**
 * Wraps an existing {@link PageGraph} with a cache. This is a read only view on the page graph, and assumes that the
 * graph being cached is not going to change while this cache is being used. That means that any modification to the
 * underlying graph may not be represented in the cached values returned by this graph.
 */
public class CachedPageGraph implements PageGraph {

    private final PageGraph graph;
    private final int graphSize;
    private final Set<Integer> pageIDs;
    private final LoadingCache<Integer, Set<Integer>> graphCache;
    private final LoadingCache<Integer, Integer> graphNodeSizeCache;

    CachedPageGraph(PageGraph graph) {
        this.graph = graph;
        this.pageIDs = graph.getPageIDs();
        this.graphSize = pageIDs.size();

        graphCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .build(new CacheLoader<Integer, Set<Integer>>() {
                public Set<Integer> load(Integer pageID) {
                    return graph.getOutboundLinks(pageID);
                }
            });

        graphNodeSizeCache = CacheBuilder.newBuilder()
            .maximumSize(graphSize)
            .build(new CacheLoader<Integer, Integer>() {
                public Integer load(Integer pageID) {
                    return getOutboundLinks(pageID).size();
                }
            });
    }

    @Override
    public String toDomain(Integer id) {
        return graph.toDomain(id);
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
    @SneakyThrows
    public int size(int pageID) {
        return graphNodeSizeCache.get(pageID);
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
    public Set<String> getOutboundLinks(String domain) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public PageGraph pruneDanglingPages(int iterationCount) {
        throw new UnsupportedOperationException("this cache is read only!");
    }

    @Override
    public Set<Integer> merge(PageGraph otherGraph) {
        throw new UnsupportedOperationException("this cache is read only!");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("this cache is read only! try closing the graph being cached");
    }
}
