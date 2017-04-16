package com.iodice.crawler.worker.queue;

class QueueException extends Exception {
    QueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
