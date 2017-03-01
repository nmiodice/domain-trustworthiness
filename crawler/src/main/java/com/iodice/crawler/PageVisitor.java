package com.iodice.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PageVisitor extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(PageVisitor.class);
    private static Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|JPE?G|png|tiff?|ico|nef|raw|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g"
            + "|avi|mov|mpeg|ram|m4v|wmv|rm|smil|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx|swf"
            + "|zip|rar|gz|bz2|7z|bin|xml|txt|java|c|cpp|exe))$");

    private PageGraph pageGraph;

    PageVisitor(PageGraph pageGraph) {
        this.pageGraph = pageGraph;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return shouldVisitUrl(url.getURL());
    }

    private boolean shouldVisitUrl(String url) {
        return !FILTERS.matcher(url.toLowerCase()).matches();
    }

    @Override
    public void visit(Page page) {
        List<WebURL> visitableOutgoingLinks = page.getParseData()
            .getOutgoingUrls()
            .stream()
            .filter(webUrl -> shouldVisitUrl(webUrl.getURL()))
            .collect(Collectors.toList());

        logger.info("page " + page.getWebURL().getDomain() + " has " + visitableOutgoingLinks.size() + " links");

        for (WebURL outgoingURL : visitableOutgoingLinks) {
            pageGraph.add(page.getWebURL().getDomain(), outgoingURL.getDomain());
        }
    }
}
