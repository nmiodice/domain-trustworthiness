package com.iodice.crawler.webcrawler;

public class CrawlerException extends Exception {
    CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    CrawlerException(String message) {
        super(message);
    }
}
