package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.request.RequestWorkerPool;
import com.iodice.crawler.scheduler.response.ResponseWorkerPool;

import java.util.Arrays;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");
//        PersistenceAdaptor persistence = new PersistenceAdaptor();
//        persistence.storeURLEdges("source", Arrays.asList("d1", "d2"));
//        persistence.storeURLEdges("source", Arrays.asList("d1", "d2"));

//        System.out.println(persistence.getURLEdges("source"));
        ResponseWorkerPool responseWorkers = new ResponseWorkerPool(Config.getInt("worker.response.worker_count"));
        responseWorkers.start();
        RequestWorkerPool requestWorkers = new RequestWorkerPool(Config.getInt("worker.request.worker_count"));
        requestWorkers.start();
    }
}
