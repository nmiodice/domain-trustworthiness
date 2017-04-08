package com.iodice.crawler.webcrawler;

public class CrawlerException extends Exception {
    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlerException(String message) {
        super(message);
    }
}
