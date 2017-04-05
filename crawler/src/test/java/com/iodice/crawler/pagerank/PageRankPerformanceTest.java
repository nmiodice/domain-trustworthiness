package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import lombok.AllArgsConstructor;
import org.junit.Ignore;

public class PageRankPerformanceTest {
    private static final Integer[] STRESS_TEST_ITERATION_VALUES = new Integer[] { 1, 2, 4, 6, 8, 12 };
    private static final Integer PAGE_COUNT = 10000;
    private static final Integer OUTBOUND_EDGE_COUNT = 50;

    private static final TestData[] TESTS = new TestData[] {
        new TestData(PageGraphFactory.memoryDBBackedPageGraph(), "in-memory-graph"),
        new TestData(PageGraphFactory.fileDBBackedPageGraph(), "on-disk-graph") };

    private static void timeGraphInitialization(TestData test) {
        long start = System.currentTimeMillis();
        for (int sourcePage = 0; sourcePage < PAGE_COUNT - OUTBOUND_EDGE_COUNT; sourcePage++) {
            for (int destPage = sourcePage + 1; destPage <= sourcePage + OUTBOUND_EDGE_COUNT; destPage++) {
                test.graph.add(String.format("www.%d.com", sourcePage), String.format("www.%d.com", destPage));
            }
        }
        long end = System.currentTimeMillis();

        String timeFmt = String.format("%.2f", (end - start) / 1000.0);
        System.out.printf("graph init: %-20s %-20s\n", test.type, timeFmt);
    }

    @Ignore
    public void runSuite() {
        for (TestData test : TESTS) {
            timeGraphInitialization(test);
        }
        System.out.println("");
        for (TestData test : TESTS) {
            timePageRank(test);
        }
    }

    private void timePageRank(TestData test) {
        for (Integer iterations : STRESS_TEST_ITERATION_VALUES) {
            time(test, iterations);
        }
    }

    private void time(TestData test, int iterationCount) {
        long start = System.currentTimeMillis();
        PageRankCalculator.computeMany(test.graph, iterationCount);
        long end = System.currentTimeMillis();

        String timeFmt = String.format("%.2f", (end - start) / 1000.0);
        System.out.printf("page rank: %-20s %-5d %-20s\n", test.type, iterationCount, timeFmt);
    }

    @AllArgsConstructor
    private static class TestData {
        private PageGraph graph;
        private String type;
    }
}
