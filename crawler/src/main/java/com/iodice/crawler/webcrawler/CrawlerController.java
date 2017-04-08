package com.iodice.crawler.webcrawler;


import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import lombok.Getter;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class CrawlerController {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    private List<String> seeds;
    private CrawlController controller;
    private final String storageDirectory = Config.getString("crawler.storage_directory");

    @Getter
    private PageGraph pageGraph;

    private CrawlerController() {
    }

    public CrawlerController(List<String> seeds) {
        this();
        Validate.notNull(seeds, "cannot initialize crawler with null seed list");
        Validate.isFalse(seeds.isEmpty(), "cannot initialize crawler with 0 seeds");
        Validate.noNullElements(seeds.toArray(), "cannot initialize crawler with null seed elements");
        this.seeds = seeds;
    }

    public void start() throws CrawlerException {
        logger.info("starting crawler");
        if (controller != null) {
            throw new IllegalStateException("crawler already started");
        }

        try {
            this.pageGraph = PageGraphFactory.fileDBBackedPageGraph();
        } catch (Exception e) {
            throw new CrawlerException("error initializing page graph", e);
        }

        File storage = new File(storageDirectory);
        if (!storage.exists() && !storage.mkdirs()) {
            throw new CrawlerException("unable to create storage directory");
        }

        try {
            controller = buildCrawlerController();
            controller.startNonBlocking(new PageVisitorFactory(pageGraph), 10);
            logger.info("crawler started");
        } catch (Exception e) {
            logger.error("crawler could not be started: " + e.getMessage(), e);
            throw new CrawlerException("error starting crawler: " + e.getMessage(), e);
        }
    }

    private CrawlConfig buildCrawlerConfiguration() {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(storageDirectory);
        config.setPolitenessDelay(100);
        config.setResumableCrawling(false);
        config.setFollowRedirects(true);
        config.setIncludeBinaryContentInCrawling(false);
        config.setIncludeHttpsPages(true);
        config.setShutdownOnEmptyQueue(true);
        config.setMaxDepthOfCrawling(100);

        return config;
    }

    private CrawlController buildCrawlerController() throws Exception {
        CrawlConfig config = buildCrawlerConfiguration();
        PageFetcher pageFetcher = new PageFetcher(config);

        // RobotstxtServer is a memory hog if enabled
        RobotstxtConfig robotsTxtConfig = new RobotstxtConfig();
        robotsTxtConfig.setEnabled(false);

        RobotstxtServer robotstxtServer = new RobotstxtServer(robotsTxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        for (String seed : seeds) {
            controller.addSeed(seed);
        }

        return controller;
    }

    public void stop() {
        logger.info("stopping crawler");
        controller.shutdown();
        controller.waitUntilFinish();
        logger.info("crawler stopped");
    }
}
