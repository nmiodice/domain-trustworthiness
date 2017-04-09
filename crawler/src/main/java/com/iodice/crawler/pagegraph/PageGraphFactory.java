package com.iodice.crawler.pagegraph;

public class PageGraphFactory {

    public static PageGraph readOnlyCachedGraph(PageGraph graph) {
        return new CachedPageGraph(graph);
    }

    public static PageGraph memoryDBBackedPageGraph() {
        return new MapDBPageGraph(MapDBPageGraph.DBType.MEMORY);
    }

    public static PageGraph fileDBBackedPageGraph() {
        return new MapDBPageGraph(MapDBPageGraph.DBType.FILE);
    }
}
