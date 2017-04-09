package com.iodice.crawler.pagerank;

import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class PageRankCalculatorTest {
    private static final double EQ_THRESHOLD = 0.0000000000001;
    private static final PageGraph dangingGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final PageGraph smallGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final PageGraph simpleFlowGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final PageGraph minimalGraph = PageGraphFactory.memoryDBBackedPageGraph();
    private static final PageGraph largeGraph = PageGraphFactory.memoryDBBackedPageGraph();

    private static final IterativePageRankCalculator iterativeCalculator = new IterativePageRankCalculator();
    private static final MatrixPageRankCalculator matrixCalculator = new MatrixPageRankCalculator();
    private static final FilePageRankCalculator fileCalculator;

    static {
        File scratchFile = new File("/Users/nickio/personal/domain-trustworthiness/graph");
        scratchFile.mkdir();
        fileCalculator = new FilePageRankCalculator(scratchFile.toPath());
    }

    @BeforeClass
    public static void init() throws Exception {
        Config.init("config.crawler");
        initGraphs();
    }

    @AfterClass
    public static void teardown() {
        smallGraph.close();
        dangingGraph.close();
    }

    private static void initGraphs() {
        simpleFlowGraph.add("www.0.com", "www.1.com");
        simpleFlowGraph.add("www.0.com", "www.2.com");
        simpleFlowGraph.add("www.1.com", "www.3.com");
        simpleFlowGraph.add("www.2.com", "www.3.com");
        simpleFlowGraph.add("www.3.com", "www.0.com");

        smallGraph.add("www.1.com", "www.0.com");
        smallGraph.add("www.2.com", "www.0.com");
        smallGraph.add("www.0.com", "www.3.com");
        smallGraph.add("www.3.com", "www.1.com");
        smallGraph.add("www.3.com", "www.2.com");

        dangingGraph.add("www.0.com", "www.1.com");
        dangingGraph.add("www.1.com", "www.2.com");
        dangingGraph.add("www.2.com", "www.0.com");
        dangingGraph.add("www.2.com", "www.3.com");

        minimalGraph.add("www.0.com", "www.1.com");

        for (int i = 0; i < 100; i++) {
            for (int j = i - 10; j < i; j++) {
                largeGraph.add(String.format("www.%d.com", i), String.format("www.%d.com", j));
            }
        }
    }

    @Test
    public void iterativeCalculator_shouldSumToOne() {
        runRankAssertionTest(iterativeCalculator, "iterative calculator");
    }

    @Test
    public void matrixCalculator_shouldSumToOne() {
        runRankAssertionTest(matrixCalculator, "matrix calculator");
    }

    @Test
    public void fileCalculator_shouldSumToOne() {
        runRankAssertionTest(fileCalculator, "file calculator");
    }

    private void runRankAssertionTest(PageRankCalculator calculator, String name) {
        assertRankSumsToOne(calculator.computeMany(smallGraph, 30), name + ": smallGraph");
        assertRankSumsToOne(calculator.computeMany(dangingGraph, 30), name + ": dangingGraph");
        assertRankSumsToOne(calculator.computeMany(minimalGraph, 30), name + ": minimalGraph");
        assertRankSumsToOne(calculator.computeMany(largeGraph, 30), name + ": largeGraph");
        assertRankSumsToOne(calculator.computeMany(simpleFlowGraph, 30), name + ": simpleFlowGraph");
    }

    private void assertRankSumsToOne(PageRank pageRank, String failMessage) {
        assertEquals(failMessage, 1, pageRank.toMap()
            .values()
            .stream()
            .mapToDouble(i -> i)
            .sum(), EQ_THRESHOLD);
    }
}