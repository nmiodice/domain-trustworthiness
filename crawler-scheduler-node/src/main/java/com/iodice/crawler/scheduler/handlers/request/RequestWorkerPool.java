package com.iodice.crawler.scheduler.handlers.request;

import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptorFactory;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;
import com.iodice.crawler.scheduler.threads.ExecutorWrapperBase;

public class RequestWorkerPool extends ExecutorWrapperBase {

    public RequestWorkerPool(int threadCount) {
        super(threadCount);
    }

    @Override
    protected Runnable getJob() {
        return new RequestWorker(PersistenceAdaptorFactory.defaultAdaptor(), new RequestQueueAdaptor());
    }
}
