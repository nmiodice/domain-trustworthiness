package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.response.ResponseWorker;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");
        new ResponseWorker().start();
    }
}
