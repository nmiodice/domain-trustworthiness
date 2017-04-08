package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;

public interface PageRankCalculator {
    PageRank computeMany(PageGraph graph, int iterationCount);
}
