package com.iodice.crawler.scheduler;

import com.iodice.config.Config;


public class Application {
    public static void main(String[] args) {
        Config.init("config.db", "config.crawler");

        System.out.println("hello!");
    }
}
