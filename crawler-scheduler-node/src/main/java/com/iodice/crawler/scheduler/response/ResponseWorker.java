package com.iodice.crawler.scheduler.response;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.queue.ResponseQueueAdaptor;
import com.iodice.crawler.scheduler.threads.Looper;

class ResponseWorker extends Looper {

    private ResponseQueueAdaptor responseQueue;
    private ResponseHandlerPipeline responseHandler;

    ResponseWorker() {
        super();
        responseQueue = new ResponseQueueAdaptor();
        responseHandler = ResponseHandlerPipelineFactory.defaultPipeline();
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
