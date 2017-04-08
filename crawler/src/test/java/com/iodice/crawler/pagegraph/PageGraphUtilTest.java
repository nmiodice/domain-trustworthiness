package com.iodice.crawler.pagegraph;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import static org.junit.Assert.assertTrue;

public class PageGraphUtilTest {
    @Test
    public void roundTrip_shouldBeSatisfied() {
        DB db = DBMaker.memoryDB().make();
        PageGraphUtil util = new PageGraphUtil(db);
        for (int i = 0; i < 100; i++) {
            String expected = toDomain(i);
            assertTrue(util.domain(util.toPageID(expected)).equals(expected));
        }
        cleanup(db);
    }

    private void cleanup(DB db) {
        db.commit();
        db.close();
    }

    @Test
    public void measureMemoryUsages() {
        for(int i = 0; i < 5; i++) {
            DB db = DBMaker.memoryDB().make();
            measureMemoryUsageForDB(db, "memory db");
            cleanup(db);
        }
        for(int i = 0; i < 5; i++) {
            DB db = DBMaker.tempFileDB().make();
            measureMemoryUsageForDB(db, "file db");
            cleanup(db);
        }
    }

    /**
     * Attempts to measure the memory usage of a {@link PageGraphUtil} under load. The approach used is only an
     * approximation, and it relies on this method:
     *  1. run GC, measure memory
     *  2. put {@link PageGraphUtil} under load
     *  3. run GC, measure memory
     *
     * @param db {@link DB} used to initialize the {@link PageGraphUtil}
     * @param type used for logging
     */
    private void measureMemoryUsageForDB(DB db, String type) {
        PageGraphUtil util = new PageGraphUtil(db);
        Runtime runtime = Runtime.getRuntime();

        double memoryBefore = getCurrentMemoryUsage(runtime);
        for (int i = 0; i < 10000; i++) {
            util.toPageID(toDomain(i));
        }

        double memoryAfter = getCurrentMemoryUsage(runtime);
        double diff = (memoryAfter - memoryBefore) / 1000000.0;

        System.out.println(String.format("mem used: %s: %f", type, diff));
    }

    private long getCurrentMemoryUsage(Runtime runtime) {
        System.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private String toDomain(int i) {
        return String.format("www.hello world %d.com", i);
    }
}
