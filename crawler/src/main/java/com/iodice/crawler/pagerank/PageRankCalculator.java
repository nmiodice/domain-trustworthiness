package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import lombok.Getter;

@Getter
public class PageRankCalculator {

    public static final double DAMPING_FACTOR = 0.85;

    private PageGraph graph;
    /**
     * count of nodes. this is a double because whenever it needs to be used to produce fractional values in
     * mathematical calculations
     */
    private double nodeCount;

    public PageRankCalculator(PageGraph graph) {
        graph.addReverseDanglingPageLinks();
        this.graph = graph;
        this.nodeCount = graph.size();
    }

    public PageRank calculatePageRank(int iterationCount) {
        graph.addReverseDanglingPageLinks();
        PageRank pageRank = getInitialPageRank();
        for (int i = 0; i < iterationCount; i++) {
            pageRank = doSinglePageRankIteration(pageRank);
        }

        return pageRank;
    }

    PageRank getInitialPageRank() {
        PageRank pageRank = new PageRank();
        for (Integer pageID : graph.getPageIDs()) {
            pageRank.setRank(pageID, 1.0 / nodeCount);
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

        for (Integer pageID : graph.getPageIDs()) {
            double additionalRank = incoming.getRank(pageID) / graph.getOutboundLinks(pageID)
                .size();

            for (Integer outgoingPageID : graph.getOutboundLinks(pageID)) {
                outgoing.setRank(outgoingPageID, outgoing.getRankWithDefault(outgoingPageID, 0.0) + additionalRank);
            }
        }

        return outgoing;
    }
}
