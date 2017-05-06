package com.iodice.crawler.scheduler.handlers;

public interface PayloadHandler<T> {

    T handle(T payload);
}
