package com.iodice.crawler.scheduler.handlers.request;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import com.iodice.crawler.scheduler.handlers.request.handler.RequestHandlerPipelineFactory;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;
import com.iodice.crawler.scheduler.threads.LoopingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestWorker extends LoopingWorker {
    private static final Logger logger = LoggerFactory.getLogger(RequestWorker.class);
    private static final int MAX_REQUESTS_PER_MESSAGE = Config.getInt("sqs.request.max_job_per_message");

    private PersistenceAdaptor persistence;
    private PayloadHandler<WorkRequest> requestHandler;

    RequestWorker(PersistenceAdaptor persistence, RequestQueueAdaptor requestQueue) {
        super();
        this.persistence = persistence;
        this.requestHandler = RequestHandlerPipelineFactory.defaultPipeline(persistence, requestQueue);
    }

    @Override
    public void doOneWorkLoop() throws Exception {
        List<String> nextRequestURLs = persistence.getNexQueuedDomains(MAX_REQUESTS_PER_MESSAGE);
        if (!nextRequestURLs.isEmpty()) {
            requestHandler.handle(WorkRequest.builder()
                .urls(nextRequestURLs)
                .build());
        }
    }

    @Override
    public long getTimeBetweenLoopsInMS() {
        return Config.getInt("worker.request.time_between_requests");
    }
}
