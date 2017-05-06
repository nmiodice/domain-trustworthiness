package com.iodice.crawler.scheduler.handlers.response;

import com.iodice.crawler.scheduler.threads.ExecutorWrapperBase;

public class ResponseWorkerPool extends ExecutorWrapperBase {

    public ResponseWorkerPool(int threadCount) {
        super(threadCount);
    }

    @Override
    protected Runnable getJob() {
        return new ResponseWorker();
    }
}
