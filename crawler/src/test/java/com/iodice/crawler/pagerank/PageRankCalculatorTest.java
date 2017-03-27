package com.iodice.crawler.pagerank;

import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRankCalculatorTest {

    private static PageGraph simpleGraph;

    private static PageRankCalculator simplePageRankCalculator;

    @BeforeClass
    public static void init() throws Exception {
        Config.init("config.crawler");

        simpleGraph = PageGraphFactory.fileBackedPageGraph();

        simpleGraph.add("www.a.com", "www.b.com");
        simpleGraph.add("www.b.com", "www.c.com");
        simpleGraph.add("www.c.com", "www.d.com");
        simpleGraph.add("www.d.com", "www.a.com");
        simpleGraph.add("www.d.com", "www.q.com");

        simplePageRankCalculator = new PageRankCalculator(simpleGraph);
    }

    @Test
    public void getInitialRanks_shouldSumToOne() throws Exception {
        assertRankSumsToOne(simplePageRankCalculator.getInitialPageRank());
    }

    @Test
    public void getRanks_shouldSumToOneAfterOneIteration() {
        assertRankSumsToOne(simplePageRankCalculator.calculatePageRank(1));
    }

    @Test
    public void getRanks_shouldSumToOneAfterManyIterations() {
        assertRankSumsToOne(simplePageRankCalculator.calculatePageRank(30));
    }

    @Test
    public void applyDampingFactor_shouldDampen() {
        PageRank pageRank = new PageRank();
        pageRank.setRank(0, 0.0);
        pageRank.setRank(1, 0.25);
        pageRank.setRank(2, 0.35);
        pageRank.setRank(3, 0.40);
        pageRank.setRank(4, 0.0);

        simpleGraph.addReverseDanglingPageLinks();
        PageRank dampened = simplePageRankCalculator.applyDamping(pageRank);

        assertEquals(0.03, dampened.getRank(0), 0.0000000000001);
        assertEquals(0.2425, dampened.getRank(1), 0.0000000000001);
        assertEquals(0.3275, dampened.getRank(2), 0.0000000000001);
        assertEquals(0.37, dampened.getRank(3), 0.0000000000001);
        assertEquals(0.03, dampened.getRank(4), 0.0000000000001);
        assertRankSumsToOne(dampened);
    }

    private void assertRankSumsToOne(PageRank pageRank) {
        assertEquals(1, pageRank.toSet().values().stream().mapToDouble(i -> i).sum(), 0.0000000000001);
    }

}