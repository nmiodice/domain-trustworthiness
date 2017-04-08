package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.apache.commons.lang.Validate;

import java.util.Set;

public class IterativePageRankCalculator implements PageRankCalculator {

    private static final double DAMPING_FACTOR = 0.85;
    private static final int DANGLING_REMOVE_ITERATION_COUNT = 10;

    IterativePageRankCalculator() {
    }

    /**
     * The general algorithm here is the same as outlined in the original page rank implementation. Roughly speaking,
     * the steps are:
     * 1. prune dangling links for {@link #DANGLING_REMOVE_ITERATION_COUNT} iterations
     * 2. calculate the page rank of the remaining nodes
     * 3. merge the pruned dangling links back into the graph
     * 4. calculate page rank for all nodes for {@link #DANGLING_REMOVE_ITERATION_COUNT} iterations
     *
     * @param graph          the un-pruned graph for which page rank should be calculated
     * @param iterationCount the number of iterations used after pruning but before merging
     * @return page rank for the graph
     */
    public PageRank computeMany(PageGraph graph, int iterationCount) {
        Validate.isTrue(iterationCount > 0);

        PageGraph danglingCache = PageGraphFactory.cachedReadOnlyPageGraph(
            graph.pruneDanglingPages(DANGLING_REMOVE_ITERATION_COUNT));
        PageGraph prunedCache = PageGraphFactory.cachedReadOnlyPageGraph(graph);

        PageRank prunedRank = computeMany(initialRank(prunedCache), prunedCache, iterationCount);
        prunedRank.foldInPageIDs(graph.merge(danglingCache));

        PageGraph fullGraphCache = PageGraphFactory.cachedReadOnlyPageGraph(graph);
        return computeMany(prunedRank, fullGraphCache, DANGLING_REMOVE_ITERATION_COUNT);
    }

    private PageRank computeMany(PageRank incomingRank, PageGraph graph, int iterationCount) {
        if (iterationCount < 0) {
            return incomingRank;
        }
        return computeMany(computeOnce(incomingRank, graph), graph, --iterationCount);
    }

    private PageRank computeOnce(PageRank incoming, PageGraph graph) {
        return randomSurfer(basicPageRank(incoming, graph), graph);
    }

    private PageRank basicPageRank(PageRank incoming, PageGraph graph) {
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

    private PageRank randomSurfer(PageRank incoming, PageGraph graph) {
        PageRank outgoing = new PageRank();
        double basePageRankAddition = (1.0 - DAMPING_FACTOR) / graph.size();

        for (Integer pageID : incoming.getPageIDs()) {
            double dampenedValue = basePageRankAddition + DAMPING_FACTOR * incoming.getRank(pageID);
            outgoing.setRank(pageID, dampenedValue);
        }

        return outgoing;
    }

    PageRank initialRank(PageGraph graph) {
        PageRank pageRank = new PageRank();
        for (Integer pageID : graph.getPageIDs()) {
            pageRank.setRank(pageID, 1.0 / graph.size());
        }
        return pageRank;
    }
}
