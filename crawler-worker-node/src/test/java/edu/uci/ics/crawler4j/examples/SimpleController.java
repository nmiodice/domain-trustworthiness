package edu.uci.ics.crawler4j.examples;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotsTxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotsTxtServer;

public class SimpleController {
    public static void main(String[] args) throws Exception {
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setPolitenessDelay(1000);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotsTxtConfig robotsTxtConfig = new RobotsTxtConfig();
        RobotsTxtServer robotsTxtServer = new RobotsTxtServer(robotsTxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotsTxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("http://www.ics.uci.edu/~lopes/");
        controller.addSeed("http://www.ics.uci.edu/~welling/");
        controller.addSeed("http://www.ics.uci.edu/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.startNonBlocking(SimplePageVisitor.class, numberOfCrawlers);

        Thread.sleep(5000);
        controller.shutdown();
    }

}
