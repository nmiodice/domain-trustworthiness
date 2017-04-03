package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import lombok.Getter;

@Getter
public class PageRankCalculator {

    public static final double DAMPING_FACTOR = 0.85;
    public static final int DANGLING_REMOVE_ITERATION_COUNT = 10;

    private PageGraph prunedGraphArtifact;
    private PageGraph prunedGraphCache;
    private PageGraph prunedGraph;

    public PageRankCalculator(PageGraph graph) {
        this.prunedGraphArtifact = graph.collectAndRemoveDanglingPages(DANGLING_REMOVE_ITERATION_COUNT);
        this.prunedGraph = graph;
        this.prunedGraphCache = PageGraphFactory.cachedReadOnlyPageGraph(prunedGraph);
    }

    public PageRank calculatePageRank(int iterationCount) {
        PageRank pageRank = getInitialPageRank();
        for (int i = 0; i < iterationCount; i++) {
            pageRank = doSinglePageRankIteration(pageRank);
        }

        return pageRank;
    }

    PageRank getInitialPageRank() {
        PageRank pageRank = new PageRank();
        for (Integer pageID : prunedGraphCache.getPageIDs()) {
            pageRank.setRank(pageID, 1.0 / prunedGraphCache.size());
        }
        return pageRank;
    }

    private PageRank doSinglePageRankIteration(PageRank incoming) {
        return applyDamping(doSingleNaiveIteration(incoming));
    }

    PageRank applyDamping(PageRank incoming) {
        PageRank outgoing = new PageRank();
        double basePageRankAddition = (1.0 - DAMPING_FACTOR) / incoming.size();

        for (Integer pageID : incoming.getPageIDs()) {
            double dampenedValue = basePageRankAddition + DAMPING_FACTOR * incoming.getRank(pageID);
            outgoing.setRank(pageID, dampenedValue);
        }

        return outgoing;
    }

    PageRank doSingleNaiveIteration(PageRank incoming) {
        PageRank outgoing = new PageRank();

        for (Integer pageID : prunedGraphCache.getPageIDs()) {
            double additionalRank = incoming.getRank(pageID) / prunedGraphCache.getOutboundLinks(pageID)
                .size();

            for (Integer outgoingPageID : prunedGraphCache.getOutboundLinks(pageID)) {
                outgoing.setRank(outgoingPageID, outgoing.getRankWithDefault(outgoingPageID, 0.0) + additionalRank);
            }
        }

        return outgoing;
    }
}
