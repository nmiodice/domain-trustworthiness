package com.iodice.crawler.worker.pages;

import com.iodice.crawler.worker.queue.WorkQueueAdaptor;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotsTxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotsTxtServer;

public class PageParseController {
    private static final int CRAWLER_COUNT = 6;

    public void start() throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setPolitenessDelay(100);
        config.setShutdownOnEmptyQueue(false);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotsTxtConfig robotsTxtConfig = new RobotsTxtConfig();
        RobotsTxtServer robotsTxtServer = new RobotsTxtServer(robotsTxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotsTxtServer, new WorkQueueAdaptor());

        controller.start(PageVisitor.class, CRAWLER_COUNT);
    }
}
