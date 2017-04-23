package com.iodice.crawler.scheduler.response;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.queue.ResponseQueueAdaptor;
import com.iodice.crawler.scheduler.response.handler.ResponseHandlerPipeline;
import com.iodice.crawler.scheduler.response.handler.ResponseHandlerPipelineFactory;
import com.iodice.crawler.scheduler.threads.LoopingWorker;

class ResponseWorker extends LoopingWorker {

    private ResponseQueueAdaptor responseQueue;
    private ResponseHandlerPipeline responseHandler;

    ResponseWorker() {
        super();
        responseQueue = new ResponseQueueAdaptor();
        responseHandler = ResponseHandlerPipelineFactory.defaultPipeline(new PersistenceAdaptor());
    }

    @Override
    public void doOneWorkLoop() throws Exception {
        WorkResponse response = responseQueue.nextResponse();
        responseHandler.handle(response);
    }

    @Override
    public long getTimeBetweenLoopsInMS() {
        return Config.getInt("worker.response.time_between_requests");
    }
}
