package com.iodice.crawler.pagegraph;

public class PageGraphFactory {
    public static PageGraph berkeleyBackedPageGraph() throws PageGraphException {
        return new BerkeleyDBPageGraph();
    }

    public static PageGraph hashMapBackedPageGraph() {
        return new HashMapPageGraph();
    }

    public static PageGraph fileBackedPageGraph() {
        return new FileSystemPageGraph();
    }

    public static PageGraph cachedReadOnlyPageGraph(PageGraph graph) {
        return new CachedPageGraph(graph);
    }
}
