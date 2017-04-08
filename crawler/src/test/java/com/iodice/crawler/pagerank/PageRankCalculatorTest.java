package com.iodice.crawler.pagerank;

import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRankCalculatorTest {
    private static final double EQ_THRESHOLD = 0.0000000000001;
    private static final PageGraph dangingGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final PageGraph simpleGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final PageGraph largeGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final IterativePageRankCalculator calculator = new IterativePageRankCalculator();
    @BeforeClass
    public static void init() throws Exception {
        Config.init("config.crawler");
        initSimpleGraph();
        initDanglingGraph();
        initLargeGraph();
    }

    @AfterClass
    public static void teardown() {
        simpleGraph.close();
        dangingGraph.close();
    }

    private static void initSimpleGraph() {
        simpleGraph.add("www.1.com", "www.0.com");
        simpleGraph.add("www.2.com", "www.0.com");
        simpleGraph.add("www.0.com", "www.3.com");
        simpleGraph.add("www.3.com", "www.1.com");
        simpleGraph.add("www.3.com", "www.2.com");
    }

    private static void initDanglingGraph() {
        dangingGraph.add("www.0.com", "www.1.com");
        dangingGraph.add("www.1.com", "www.2.com");
        dangingGraph.add("www.2.com", "www.0.com");
        dangingGraph.add("www.2.com", "www.3.com");
    }

    private static void initLargeGraph() {
        for (int i = 0; i < 1000; i++) {
            for (int j = i - 5; j < i; j++) {
                largeGraph.add(String.format("www.%d.com", i), String.format("www.%d.com", j));
            }
        }
    }

    @Test
    public void simpleGraphRanks_shouldSumToOne() {
        runRankAssertionTest(simpleGraph, "simple");
    }

    @Test
    public void danglingGraphRanks_shouldSumToOne() {
        runRankAssertionTest(dangingGraph, "dangling");
    }

    @Test
    public void largeGraphRanks_shouldSumToOne() {
        runRankAssertionTest(largeGraph, "large");
    }

    private void runRankAssertionTest(PageGraph graph, String name) {
        assertRankSumsToOne(calculator.initialRank(graph), name + ": initial");
        assertRankSumsToOne(calculator.computeMany(graph, 1), name + ": 1 iteration");
        assertRankSumsToOne(calculator.computeMany(graph, 30), name + ": 30 iterations");
    }

    private void assertRankSumsToOne(PageRank pageRank, String failMessage) {
        assertEquals(failMessage, 1, pageRank.toMap()
            .values()
            .stream()
            .mapToDouble(i -> i)
            .sum(), EQ_THRESHOLD);
    }
}