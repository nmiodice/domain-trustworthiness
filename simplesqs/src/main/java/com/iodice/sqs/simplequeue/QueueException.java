package com.iodice.sqs.simplequeue;

public class QueueException extends Exception {
    QueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
