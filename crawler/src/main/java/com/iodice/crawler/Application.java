package com.iodice.crawler;

import com.iodice.config.Config;
import com.iodice.crawler.webcrawler.CrawlerController;
import com.iodice.crawler.webcrawler.CrawlerException;
import com.iodice.crawler.webcrawler.PageGraph;
import com.iodice.crawler.pagerank.PageRankCalculator;
import com.iodice.crawler.persistence.PageRankStore;
import com.iodice.crawler.queue.EventQueueListener;
import com.iodice.crawler.queue.PageRankJobParams;
import com.iodice.crawler.queue.EventListenerException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        EventQueueListener listener = new EventQueueListener();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);

        while (true) {
            try {
                logger.info("waiting for next job");
                PageRankJobParams jobParams = listener.getNextJobParameters();
                logger.info("got job: " + jobParams);

                CrawlerController controller = new CrawlerController(Config.getStringList("webcrawler.seeds"));
                controller.start();

                Thread.sleep(jobParams.getRuntime() * 60 * 1000);
                controller.stop();

                logger.info("finished job: " + jobParams);

                PageGraph graph = controller.getPageGraph();
                PageRankCalculator pageRankCalculator = PageRankCalculator.builder().graph(graph).build();

                PageRankStore store = new PageRankStore();
                store.deleteAll();
                store.store(pageRankCalculator.calculatePageRank(30), graph);
            } catch (EventListenerException | CrawlerException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
