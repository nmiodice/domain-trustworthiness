package com.iodice.crawler.pagegraph;

import com.iodice.crawler.pagerank.PageRankCalculator;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PageGraphTest {
    private static final int PAGE_COUNT = 100000;
    private static final int OUT_DEGREE = 50;

    private double msToS(long s, long e) {
        return (e - s) / 1000.0;
    }

    @Test
    public void collectAndRemoveDanglingPages_shouldRemoveLevelOneDanglerInOneIteration() {
        PageGraph pg = PageGraphFactory.memoryDBBackedPageGraph();
        pg.add("www.0.com", "www.1.com");
        pg.add("www.1.com", "www.2.com");

        PageGraph danglers = pg.collectAndRemoveDanglingPages(1);

        assertEquals(2, pg.size());
        assertTrue("page graph contains wrong links", pg.getOutboundLinks(0).contains(1));

        assertEquals(2, danglers.size());
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(1).contains(2));
    }

    @Test
    public void collectAndRemoveDanglingPages_shouldRemoveLevelOneDanglerInThreeIterations() {
        PageGraph pg = PageGraphFactory.memoryDBBackedPageGraph();
        pg.add("www.0.com", "www.1.com");
        pg.add("www.1.com", "www.2.com");
        pg.add("www.2.com", "www.3.com");
        pg.add("www.3.com", "www.4.com");

        pg.add("www.0.com", "www.5.com");
        pg.add("www.1.com", "www.6.com");
        pg.add("www.2.com", "www.7.com");

        PageGraph danglers = pg.collectAndRemoveDanglingPages(3);

        assertEquals(2, pg.size());
        assertTrue("page graph contains wrong links", pg.getOutboundLinks(0).contains(1));

        assertEquals(8, danglers.size());
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(0).contains(5));
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(1).contains(2));
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(1).contains(6));
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(2).contains(3));
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(2).contains(7));
        assertTrue("page graph contains wrong links", danglers.getOutboundLinks(3).contains(4));
    }

    @Test
    public void merge_shouldContainOldAndNewValues() {
        PageGraph primary = PageGraphFactory.memoryDBBackedPageGraph();
        primary.add("www.0.com", "www.1.com");
        primary.add("www.1.com", "www.2.com");

        PageGraph secondary = PageGraphFactory.memoryDBBackedPageGraph();
        secondary.add("www.3.com", "www.4.com");
        secondary.add("www.5.com", "www.6.com");

        primary.merge(secondary);
        assertEquals("merged results have wrong size", 7, primary.size());
        assertTrue("merged results are missing values", primary.getOutboundLinks(0).contains(1));
        assertTrue("merged results are missing values", primary.getOutboundLinks(1).contains(2));
        assertTrue("merged results are missing values", primary.getOutboundLinks(3).contains(4));
        assertTrue("merged results are missing values", primary.getOutboundLinks(5).contains(6));
    }

    @Ignore("ignore")
    public void stressTest() throws Exception {
        PageGraph pg = PageGraphFactory.fileDBBackedPageGraph();
        long start, end;

        // adding elements to graph
        start = System.currentTimeMillis();
        for (int srcPage = 0; srcPage < PAGE_COUNT; srcPage++) {
            for (int destPage = srcPage - 1; destPage >= srcPage - OUT_DEGREE; destPage--) {
                pg.add(String.valueOf(srcPage), String.valueOf(destPage));
            }
        }
        end = System.currentTimeMillis();
        System.out.println("add: " + msToS(start, end));

        // prepping graph for page rank calculation
        //        start = System.currentTimeMillis();
        //        PageRankCalculator calc = new PageRankCalculator(pg);
        //        calc.calculatePageRank(30);
        //        end = System.currentTimeMillis();
        //        System.out.println("calculatePageRank: " + msToS(start, end));

//        start = System.currentTimeMillis();
//        PageRankCalculator calc = new PageRankCalculator(PageGraphFactory.cachedReadOnlyPageGraph(pg));
//        calc.calculatePageRank(30);
//        end = System.currentTimeMillis();
//        System.out.println("cached calculatePageRank: " + msToS(start, end));

        Thread.sleep(30 * 1000);
    }
}
