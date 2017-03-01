package com.iodice;

import com.iodice.config.Config;
import com.iodice.crawler.CrawlerController;
import com.iodice.crawler.CrawlerException;
import com.iodice.crawler.PageGraph;
import com.iodice.pagerank.PageRankCalculator;
import com.iodice.persistence.PageRankStore;
import com.iodice.queue.EventListenerException;
import com.iodice.queue.EventQueueListener;
import com.iodice.queue.PageRankJobParams;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class main {
    private static final Logger logger = LoggerFactory.getLogger(main.class);

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

                CrawlerController controller = new CrawlerController(Config.getStringList("crawler.seeds"));
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
