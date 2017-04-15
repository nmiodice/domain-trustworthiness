package com.iodice.crawler.worker;

import com.iodice.config.Config;
import com.iodice.crawler.worker.pages.PageParseController;

public class Application {
    public static void main(String[] args) throws Exception {
        Config.init("config.worker");
        new PageParseController().start();
    }
}
