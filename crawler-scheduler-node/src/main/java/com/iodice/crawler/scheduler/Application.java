package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.handlers.request.RequestWorkerPool;
import com.iodice.crawler.scheduler.handlers.response.ResponseWorkerPool;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;

import java.util.Collections;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");

        ResponseWorkerPool responseWorkers = new ResponseWorkerPool(Config.getInt("worker.response.worker_count"));
        responseWorkers.start();
        RequestWorkerPool requestWorkers = new RequestWorkerPool(Config.getInt("worker.request.worker_count"));
        requestWorkers.start();

        // start workers off with a well-known seed URL
        new RequestQueueAdaptor().emitRequest(WorkRequest.builder()
            .urls(Config.getStringList("scheduler.seeds"))
            .build());
    }
}
