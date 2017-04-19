package com.iodice.crawler.scheduler.request;

import com.iodice.crawler.scheduler.threads.ExecutorWrapperBase;

public class RequestWorkerPool extends ExecutorWrapperBase {

    public RequestWorkerPool(int threadCount) {
        super(threadCount);
    }
    @Override
    protected Runnable getJob() {
        return new RequestWorker();
    }
}
