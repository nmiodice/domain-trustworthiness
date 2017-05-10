package com.iodice.crawler.scheduler.handlers.response;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import com.iodice.crawler.scheduler.handlers.response.handler.ResponseHandlerPipelineFactory;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptorFactory;
import com.iodice.crawler.scheduler.queue.ResponseQueueAdaptor;
import com.iodice.crawler.scheduler.threads.LoopingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ResponseWorker extends LoopingWorker {
    private static final Logger logger = LoggerFactory.getLogger(ResponseWorker.class);

    private ResponseQueueAdaptor responseQueue;
    private PayloadHandler<WorkResponse> responseHandler;

    ResponseWorker() {
        super();
        responseQueue = new ResponseQueueAdaptor();
        responseHandler = ResponseHandlerPipelineFactory.defaultPipeline(PersistenceAdaptorFactory.defaultAdaptor());
    }

    @Override
    public void doOneWorkLoop() throws Exception {
        List<WorkResponse> responses = responseQueue.nextResponseBatch();
        for (WorkResponse response : responses) {
            long startMS = System.currentTimeMillis();
            responseHandler.handle(response);

            long endMS = System.currentTimeMillis();
            long deltaSec = endMS - startMS;

            logger.info(String.format("handling responses at %d ms per loop", deltaSec));
        }
    }

    @Override
    public long getTimeBetweenLoopsInMS() {
        return Config.getInt("worker.response.time_between_requests");
    }
}
