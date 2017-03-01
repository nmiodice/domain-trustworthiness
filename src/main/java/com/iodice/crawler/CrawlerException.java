package com.iodice.crawler;

public class CrawlerException extends Exception {
    CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    CrawlerException(String message) {
        super(message);
    }
}
