package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.response.ResponseWorkerPool;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");
        ResponseWorkerPool workers = new ResponseWorkerPool(1);

        workers.start();
    }
}
