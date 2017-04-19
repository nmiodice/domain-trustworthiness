package com.iodice.crawler.scheduler.request;

import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;
import com.iodice.crawler.scheduler.threads.Looper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestWorker extends Looper {
    private static final Logger logger = LoggerFactory.getLogger(RequestWorker.class);

    private RequestQueueAdaptor requestQueue;

    RequestWorker() {
        super();
        requestQueue = new RequestQueueAdaptor();
    }

    @Override
    public void doOneWorkLoop() throws Exception {
        logger.info("hello bitches!");
        Thread.sleep(1000);
    }
}
