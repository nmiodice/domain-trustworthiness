package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class PageRankCalculator {

    public static final double DAMPING_FACTOR = 0.85;

    @NonNull
    private PageGraph graph;

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
        double numPages = graph.size();
        for (Integer pageID : graph.getPageIDs()) {
            pageRank.setRank(pageID, 1.0 / numPages);
        }
        return pageRank;
    }

    private PageRank doSinglePageRankIteration(PageRank incoming) {
        return applyDamping(doSingleNaiveIteration(incoming));
    }

    PageRank applyDamping(PageRank incoming) {
        PageRank outgoing = new PageRank();

        for (Integer pageID : incoming.getPageIDs()) {
            double dampenedValue = (1.0 - DAMPING_FACTOR) / graph.size() + DAMPING_FACTOR * incoming.getRank(pageID);
            outgoing.setRank(pageID, dampenedValue);
        }

        return outgoing;
    }

    private PageRank doSingleNaiveIteration(PageRank incoming) {
        PageRank outgoing = new PageRank();

        for (Integer pageID : graph.getPageIDs()) {
            double additionalRank = incoming.getRank(pageID) / graph.getOutboundLinks(pageID).size();

            for (Integer outgoingPageID : graph.getOutboundLinks(pageID)) {
                outgoing.setRank(outgoingPageID, outgoing.getRankWithDefault(outgoingPageID, 0.0) + additionalRank);
            }
        }

        return outgoing;
    }
}
