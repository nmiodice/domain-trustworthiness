package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.threads.ExecutorWrapperBase;


public class ResponseWorkerPool extends ExecutorWrapperBase {

    ResponseWorkerPool(int threadCount) {
        super(threadCount);
    }

    @Override
    protected Runnable getJob() {
        return new ResponseWorker();
    }
}
