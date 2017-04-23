package com.iodice.crawler.scheduler.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LoopingWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LoopingWorker.class);

    private final static AtomicInteger WORKER_ID_COUNTER = new AtomicInteger();
    protected final int threadID;
    private boolean stopped = false;

    public LoopingWorker() {
        threadID = WORKER_ID_COUNTER.incrementAndGet();
    }

    public void stop() {
        logger.info(String.format("worker %d is stopping", threadID));
        stopped = true;
    }

    @Override
    public void run() {
        while (!stopped) {
            long baseWaitTime = getTimeBetweenLoopsInMS();
            try {
                if (baseWaitTime > 0) {
                    // random jitter to prevent resource thrashing
                    long randomWait = Math.max(0, baseWaitTime + ThreadLocalRandom.current()
                        .nextLong(-1000, 1000));
                    Thread.sleep(randomWait);
                }
                doOneWorkLoop();
            } catch (Exception e) {
                logger.error(String.format("worker %d encountered an exception: %s", threadID, e.getMessage()), e);
            }
        }
    }

    public abstract void doOneWorkLoop() throws Exception;

    public abstract long getTimeBetweenLoopsInMS();
}
