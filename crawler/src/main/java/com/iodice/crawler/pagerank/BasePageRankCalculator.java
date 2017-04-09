package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.apache.commons.lang.Validate;

import java.util.Set;

abstract class BasePageRankCalculator implements PageRankCalculator {
    private static final double DAMPING_FACTOR = 0.85;
    private static final int DANGLING_REMOVE_ITERATION_COUNT = 10;

    /**
     * implementation specific way of solving one iteration of page rank
     */
    abstract PageRank singPageRankIteration(PageRank incoming, PageGraph graph);

    /**
     * implementation specific initialization
     */
    abstract void init(PageGraph graph);

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
    @Override
    public PageRank computeMany(PageGraph graph, int iterationCount) {
        Validate.isTrue(iterationCount > 0);

        PageGraph danglingGraph = graph.pruneDanglingPages(DANGLING_REMOVE_ITERATION_COUNT);
        PageGraph prunedCache = PageGraphFactory.readOnlyCachedGraph(graph);

        PageRank prunedRank = initAndComputeMany(initialRank(prunedCache), prunedCache, iterationCount);
        Set<Integer> newPages = graph.merge(PageGraphFactory.readOnlyCachedGraph(danglingGraph));
        danglingGraph.close();

        prunedRank.foldInPageIDs(newPages);
        PageGraph fullGraphCache = PageGraphFactory.readOnlyCachedGraph(graph);
        return initAndComputeMany(prunedRank, fullGraphCache, DANGLING_REMOVE_ITERATION_COUNT);
    }

    private PageRank initAndComputeMany(PageRank startingRank, PageGraph graph, int iterationCount) {
        init(graph);
        return computeMany(startingRank, graph, iterationCount);
    }

    /**
     * Internal helper to compute page rank recursively
     *
     * @param incomingRank   old rank
     * @param graph          graph in question
     * @param iterationCount how many times to compute page rank
     * @return the newly computed page rank
     */
    private PageRank computeMany(PageRank incomingRank, PageGraph graph, int iterationCount) {
        if (iterationCount == 0) {
            return incomingRank;
        }
        return computeMany(computeOnce(incomingRank, graph), graph, --iterationCount);
    }

    /**
     * Single computation of page rank with random surfer effect added post-computation
     */
    private PageRank computeOnce(PageRank incoming, PageGraph graph) {
        return randomSurfer(singPageRankIteration(incoming, graph), graph);
    }

    /**
     * helper method to get the initial page rank for a graph.
     */
    PageRank initialRank(PageGraph graph) {
        double size = graph.size();
        PageRank pageRank = new PageRank();
        for (Integer pageID : graph.getPageIDs()) {
            pageRank.setRank(pageID, 1.0 / size);
        }
        return pageRank;
    }

    /**
     * applies random surfer effect to a page rank
     */
    private PageRank randomSurfer(PageRank incoming, PageGraph graph) {
        PageRank outgoing = new PageRank();
        double basePageRankAddition = (1.0 - DAMPING_FACTOR) / graph.size();

        for (Integer pageID : incoming.getPageIDs()) {
            double dampenedValue = basePageRankAddition + DAMPING_FACTOR * incoming.getRank(pageID);
            outgoing.setRank(pageID, dampenedValue);
        }

        return outgoing;
    }

    @Override
    public void cleanup() {
    }
}
