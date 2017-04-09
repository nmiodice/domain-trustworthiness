package com.iodice.crawler.pagerank;

import java.io.File;
import java.nio.file.Path;

public class PageRankCalculatorFactory {
    public static PageRankCalculator getIterativeCalculator() {
        return new IterativePageRankCalculator();
    }

    public static PageRankCalculator getMatrixCalculator() {
        return new MatrixPageRankCalculator();
    }

    public static PageRankCalculator getFileCalculator(Path workingDirectory) {
        return new FilePageRankCalculator(workingDirectory);
    }
}
