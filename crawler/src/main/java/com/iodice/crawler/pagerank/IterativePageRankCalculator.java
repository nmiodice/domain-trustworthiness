package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;

import java.util.Set;

class IterativePageRankCalculator extends BasePageRankCalculator {

    IterativePageRankCalculator() {
    }

    PageRank singPageRankIteration(PageRank incoming, PageGraph graph) {
        PageRank outgoing = new PageRank();
        Set<Integer> pageIDs = graph.getPageIDs();

        for (Integer pageID : pageIDs) {
            int nodeSize = graph.size(pageID);
            if (nodeSize == 0) {
                // need to add the rank because it will not otherwise be updated and the page rank would be lost
                outgoing.addRank(pageID, incoming.getRank(pageID));
            }

            double rank = incoming.getRank(pageID) / (double) nodeSize;
            for (Integer outgoingPageID : graph.getOutboundLinks(pageID)) {
                outgoing.addRank(outgoingPageID, rank);
            }

            // need to add the rank here just in case no other page points to this one
            outgoing.addRank(pageID, 0.0);
        }
        return outgoing;
    }

    @Override
    void init(PageGraph graph) {
    }
}
