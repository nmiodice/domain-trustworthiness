package com.iodice.crawler.pagerank;

/**
 * Created by nickio on 4/8/17.
 */
public class PageRankCalculatorFactory {
    public static PageRankCalculator getIterativeCalculator() {
        return new IterativePageRankCalculator();
    }
}
