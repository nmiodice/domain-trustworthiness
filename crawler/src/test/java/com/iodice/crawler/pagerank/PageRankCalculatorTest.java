package com.iodice.crawler.pagerank;

import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRankCalculatorTest {

    private static PageGraph simpleGraph;

    private static PageRankCalculator simplePageRankCalculator;

    @BeforeClass
    public static void init() throws Exception {
        Config.init("config.crawler");

        simpleGraph = PageGraph.mapDBBackedPageGraph();

        simpleGraph.add("www.a.com", "www.b.com");
        simpleGraph.add("www.b.com", "www.c.com");
        simpleGraph.add("www.c.com", "www.d.com");
        simpleGraph.add("www.d.com", "www.a.com");

        simplePageRankCalculator = PageRankCalculator.builder().graph(simpleGraph).build();
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
        pageRank.setRank(2, 0.65);
        pageRank.setRank(3, 0.85);

        PageRank dampened = simplePageRankCalculator.applyDamping(pageRank);
        assertEquals(0.0375, dampened.getRank(0), 0.0000000000001);
        assertEquals(0.25, dampened.getRank(1), 0.0000000000001);
        assertEquals(0.59, dampened.getRank(2), 0.0000000000001);
        assertEquals(0.76, dampened.getRank(3), 0.0000000000001);
    }

    private void assertRankSumsToOne(PageRank pageRank) {
        assertEquals(1, pageRank.toSet().values().stream().mapToDouble(i -> i).sum(), Double.MIN_VALUE);
    }

}