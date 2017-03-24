package com.iodice.crawler.webcrawler;

import com.iodice.crawler.pagegraph.PageGraph;
import edu.uci.ics.crawler4j.crawler.CrawlController;

public class PageVisitorFactory implements CrawlController.WebCrawlerFactory<PageVisitor> {

    private final PageGraph pageGraph;

    PageVisitorFactory(PageGraph pageGraph) {
        this.pageGraph = pageGraph;
    }

    @Override
    public PageVisitor newInstance() {
        return new PageVisitor(pageGraph);
    }
}