package com.iodice.crawler.pagegraph;

import com.iodice.crawler.pagerank.PageRank;
import com.iodice.crawler.pagerank.PageRankCalculator;
import org.junit.Test;

/**
 * Created by nickio on 3/23/17.
 */
public class PageGraphTest {
    private static final int PAGE_COUNT = 1000;
    private static final int OUT_DEGREE = 5;

    private double msToS(long s, long e) {
        return (e - s) / 1000.0;
    }
    @Test
    public void stressTest() throws Exception {
        PageGraph pg = PageGraph.berkeleyBackedPageGraph();
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
        start = System.currentTimeMillis();
        pg.addReverseDanglingPageLinks();
        end = System.currentTimeMillis();
        System.out.println("addReverseDanglingPageLinks: " + msToS(start, end));

        start = System.currentTimeMillis();
        PageRankCalculator calc = PageRankCalculator.builder().graph(pg).build();
        PageRank rank = calc.calculatePageRank(30);
        end = System.currentTimeMillis();
        System.out.println("calculatePageRank: " + msToS(start, end));
    }
}
