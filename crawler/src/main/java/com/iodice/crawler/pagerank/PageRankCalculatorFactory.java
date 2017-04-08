package com.iodice.crawler.pagerank;


public class PageRankCalculatorFactory {
    public static PageRankCalculator getIterativeCalculator() {
        return new IterativePageRankCalculator();
    }

    public static PageRankCalculator getMatrixCalculator() {
        return new MatrixPageRankCalculator();
    }
}
