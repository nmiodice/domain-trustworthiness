package com.iodice.crawler.scheduler.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ExecutorWrapperBase {
    private ExecutorService executor;
    private int threadCount;

    public ExecutorWrapperBase(int threadCount) {
        if (threadCount > 0) {
            executor = Executors.newFixedThreadPool(threadCount);
        }
        this.threadCount = threadCount;
    }

    public void start() {
        for (int i = 0; i < threadCount; i++) {
            executor.submit(getJob());
        }
    }

    protected abstract Runnable getJob();
}
