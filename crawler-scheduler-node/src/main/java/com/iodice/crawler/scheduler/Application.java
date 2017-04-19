package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.request.RequestWorkerPool;
import com.iodice.crawler.scheduler.response.ResponseWorkerPool;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");
//        ResponseWorkerPool responseWorkers = new ResponseWorkerPool(1);
//        responseWorkers.start();
        RequestWorkerPool requestWorkers = new RequestWorkerPool(1);
        requestWorkers.start();
    }
}
