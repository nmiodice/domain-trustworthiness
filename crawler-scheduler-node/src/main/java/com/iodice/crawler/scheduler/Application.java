package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.handlers.request.RequestWorkerPool;
import com.iodice.crawler.scheduler.handlers.response.ResponseWorkerPool;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptorFactory;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;
import com.iodice.crawler.scheduler.utils.URLFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");
//        PersistenceAdaptorFactory.postgresBackedAdaptor().enqueueURLs(Collections.emptyList());
//        PersistenceAdaptorFactory.postgresBackedAdaptor().enqueueURLs(Collections.singletonList("http://www.cnn.com"));
//        PersistenceAdaptorFactory.postgresBackedAdaptor().enqueueURLs(Arrays.asList("http://www.cnn.com",
//            "http://www.google.com", "http://www.google.com/ads"));
//
//        System.out.println(PersistenceAdaptorFactory.postgresBackedAdaptor().getDomainScheduledCount("www.cnn.com"));
//        System.out.println(PersistenceAdaptorFactory.postgresBackedAdaptor().getDomainScheduledCount("www.abc.com"));
//        System.out.println(PersistenceAdaptorFactory.postgresBackedAdaptor().dequeueURLs(50));

//        ResponseWorkerPool responseWorkers = new ResponseWorkerPool(Config.getInt("worker.response.worker_count"));
//        responseWorkers.start();
        RequestWorkerPool requestWorkers = new RequestWorkerPool(Config.getInt("worker.request.worker_count"));
        requestWorkers.start();

        // start workers off with a well-known seed URL
        new RequestQueueAdaptor().emitRequest(WorkRequest.builder()
            .urls(Config.getStringList("scheduler.seeds"))
            .build());
    }
}
