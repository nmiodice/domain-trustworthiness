package com.iodice.crawler.worker.pages;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PageVisitor extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(PageVisitor.class);
    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|JPE?G|png|tiff?|ico|nef|raw|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g"
            + "|avi|mov|mpeg|ram|m4v|wmv|rm|smil|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx|swf"
            + "|zip|rar|gz|bz2|7z|bin|xml|txt|java|c|cpp|exe))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return shouldVisitUrl(url.getUrl());
    }

    private boolean shouldVisitUrl(String url) {
        return !FILTERS.matcher(url.toLowerCase())
            .matches();
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            WebURL url = page.getWebURL();
            Collection<WebURL> outgoing = page.getParseData()
                .getOutgoingUrls()
                .stream()
                .filter(webUrl -> shouldVisitUrl(webUrl.getUrl()))
                .collect(Collectors.toList());

            frontier.scheduleAll(outgoing, url);
            logger.info(String.format("%d URLs scheduled from %s ", outgoing.size(),
                StringUtils.substring(url.getDomain(), 0, 20)));
        }
    }
}
