package com.iodice.crawler.scheduler.response;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResponseWorkerPool {
    private ExecutorService executor;
    private int threadCount;

    public ResponseWorkerPool(int threadCount) {
        executor = Executors.newFixedThreadPool(threadCount);
        this.threadCount = threadCount;
    }

    public void start() {
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new ResponseWorker());
        }
    }
}
