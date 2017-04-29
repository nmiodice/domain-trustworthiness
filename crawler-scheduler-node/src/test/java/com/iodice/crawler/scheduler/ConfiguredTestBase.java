package com.iodice.crawler.scheduler;

import com.iodice.config.Config;
import org.junit.BeforeClass;

public class ConfiguredTestBase {
    @BeforeClass
    public static void configure() {
        Config.init("config.db", "config.scheduler");
    }
}
