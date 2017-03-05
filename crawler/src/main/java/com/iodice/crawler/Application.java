package com.iodice.crawler;

import com.iodice.config.Config;
import com.iodice.crawler.pagerank.PageRankCalculator;
import com.iodice.crawler.persistence.PageRankStoreAdaptor;
import com.iodice.crawler.queue.EventListenerException;
import com.iodice.crawler.queue.EventQueueListener;
import com.iodice.crawler.queue.PageRankJobParams;
import com.iodice.crawler.webcrawler.CrawlerController;
import com.iodice.crawler.webcrawler.CrawlerException;
import com.iodice.crawler.webcrawler.PageGraph;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static void runCrawler(int minutes) throws InterruptedException, CrawlerException {
        CrawlerController controller = new CrawlerController(Config.getStringList("crawler.seeds"));
        controller.start();

        Thread.sleep(minutes * 60 * 1000);
        controller.stop();

        PageGraph graph = controller.getPageGraph();
        PageRankCalculator pageRankCalculator = PageRankCalculator.builder().graph(graph).build();

        PageRankStoreAdaptor store = new PageRankStoreAdaptor();
        store.deleteAll();
        store.store(pageRankCalculator.calculatePageRank(30), graph);
    }

    public static void main(String[] args) {
        Config.init("config.db", "config.crawler");

        EventQueueListener listener = new EventQueueListener();
        try {
            if (args.length > 0) {
                runCrawler(Integer.parseInt(args[0]));
            } else {
                while (true) {
                    logger.info("waiting for next job");
                    PageRankJobParams jobParams = listener.getNextJobParameters();
                    logger.info("starting job: " + jobParams);
                    runCrawler(jobParams.getRuntime());
                    logger.info("finished job: " + jobParams);
                }
            }
        } catch (EventListenerException | CrawlerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
