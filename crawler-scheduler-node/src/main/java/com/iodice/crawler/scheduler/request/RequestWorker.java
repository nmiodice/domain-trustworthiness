package com.iodice.crawler.scheduler.request;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;
import com.iodice.crawler.scheduler.threads.LoopingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestWorker extends LoopingWorker {
    private static final Logger logger = LoggerFactory.getLogger(RequestWorker.class);
    private static final int MAX_REQUESTS_PER_MESSAGE = Config.getInt("sqs.request.max_job_per_message");

    private RequestQueueAdaptor requestQueue;
    private PersistenceAdaptor persistence;

    RequestWorker(PersistenceAdaptor persistence, RequestQueueAdaptor requestQueue) {
        super();
        this.persistence = persistence;
        this.requestQueue = requestQueue;
    }

    @Override
    public void doOneWorkLoop() throws Exception {
        List<String> nextRequestURLs = persistence.getNexQueuedDomains(MAX_REQUESTS_PER_MESSAGE);
        if (!nextRequestURLs.isEmpty()) {
            requestQueue.emitRequest(WorkRequest.builder()
                .urls(nextRequestURLs)
                .build());
            logger.info(String.format("request worker %d submitted %d urls", threadID, nextRequestURLs.size()));
        }
    }

    @Override
    public long getTimeBetweenLoopsInMS() {
        return Config.getInt("worker.request.time_between_requests");
    }
}
