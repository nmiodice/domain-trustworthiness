package com.iodice.crawler.scheduler;

import com.iodice.config.Config;

public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.scheduler");
        new WorkScheduler().start();
    }
}
