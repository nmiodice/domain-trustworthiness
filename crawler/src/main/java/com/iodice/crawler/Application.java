package com.iodice.crawler;

import com.iodice.config.Config;
import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagerank.PageRankCalculator;
import com.iodice.crawler.persistence.PageRankStoreAdaptor;
import com.iodice.crawler.queue.EventListenerException;
import com.iodice.crawler.queue.EventQueueListener;
import com.iodice.crawler.queue.PageRankJobParams;
import com.iodice.crawler.webcrawler.CrawlerController;
import com.iodice.crawler.webcrawler.CrawlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static void runCrawler(int minutes) throws InterruptedException, CrawlerException {
        CrawlerController controller = new CrawlerController(Config.getStringList("crawler.seeds"));
        PageGraph graph = controller.getPageGraph();

        controller.start();

        long sleepTimeMS = minutes * 60 * 1000;
        long startTimeMS = System.currentTimeMillis();
        long waitTime = 20 * 1000;
        long sleepTimeSoFarMS = 0;

        while (sleepTimeSoFarMS < sleepTimeMS) {
            Thread.sleep(waitTime);
            sleepTimeSoFarMS = System.currentTimeMillis() - startTimeMS;
            double sleepTimeSoFarMin = sleepTimeSoFarMS / 1000.0 / 60.0;
            logger.info(
                "after sleeping for " + sleepTimeSoFarMin + " minutes, page graph has " + graph.size() + " nodes");
        }

        controller.stop();

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
