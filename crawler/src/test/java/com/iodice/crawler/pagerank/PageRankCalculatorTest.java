package com.iodice.crawler.pagerank;

import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRankCalculatorTest {
    private static final double EQ_THRESHOLD = 0.0000000000001;

    private static PageRankCalculator simpleCalculator;
    private static PageRankCalculator danglingCalculator;

    @BeforeClass
    public static void init() throws Exception {
        Config.init("config.crawler");
        initSimpleCalculator();
        initDanglingCalculator();
    }

    private static void initSimpleCalculator() throws Exception {
        PageGraph graph = PageGraphFactory.memoryDBBackedPageGraph();
        graph.add("www.1.com", "www.0.com");
        graph.add("www.2.com", "www.0.com");
        graph.add("www.0.com", "www.3.com");
        graph.add("www.3.com", "www.1.com");
        graph.add("www.3.com", "www.2.com");

        simpleCalculator = new PageRankCalculator(graph);
    }

    private static void initDanglingCalculator() throws Exception {
        PageGraph graph = PageGraphFactory.memoryDBBackedPageGraph();
        graph.add("www.0.com", "www.1.com");
        graph.add("www.1.com", "www.2.com");
        graph.add("www.2.com", "www.0.com");
        graph.add("www.2.com", "www.3.com");

        danglingCalculator = new PageRankCalculator(graph);
    }

    @Test
    public void ranks_shouldSumToOne() {
        assertRankSumsToOne(simpleCalculator.getInitialPageRank(), "simple graph, initial");
        assertRankSumsToOne(simpleCalculator.calculatePageRank(1), "simple graph, 1 iteration");
        assertRankSumsToOne(simpleCalculator.calculatePageRank(30), "simple graph, 30 iterations");
        assertRankSumsToOne(simpleCalculator.applyDamping(simpleCalculator.calculatePageRank(1)),
            "simple graph, damping");

        assertRankSumsToOne(danglingCalculator.getInitialPageRank(), "dangling graph, initial rank");
        assertRankSumsToOne(danglingCalculator.calculatePageRank(1), "dangling graph, 1 iteration");
        assertRankSumsToOne(danglingCalculator.calculatePageRank(30), "dangling graph, 30 iterations");
        assertRankSumsToOne(danglingCalculator.applyDamping(danglingCalculator.calculatePageRank(1)),
            "simple graph, damping");
    }

    private void assertRankSumsToOne(PageRank pageRank, String failMessage) {
        assertEquals(failMessage, 1, pageRank.toMap()
            .values()
            .stream()
            .mapToDouble(i -> i)
            .sum(), EQ_THRESHOLD);
    }

    @Test
    public void applyDamping_shouldDistributeRankProperly() {
        PageRank pageRank = new PageRank();
        pageRank.setRank(0, 0.0);
        pageRank.setRank(1, 0.25);
        pageRank.setRank(2, 0.35);
        pageRank.setRank(3, 0.40);
        pageRank.setRank(4, 0.0);

        PageRank dampened = simpleCalculator.applyDamping(pageRank);

        assertEquals(0.03, dampened.getRank(0), 0.0000000000001);
        assertEquals(0.2425, dampened.getRank(1), 0.0000000000001);
        assertEquals(0.3275, dampened.getRank(2), 0.0000000000001);
        assertEquals(0.37, dampened.getRank(3), 0.0000000000001);
        assertEquals(0.03, dampened.getRank(4), 0.0000000000001);
    }
}