package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.queue.WorkQueueAdaptor;

public class ResponseWorker {
    private WorkQueueAdaptor responseQueue;
    private ResponseHandlerPipeline responseHandler;

    public ResponseWorker() {
        responseQueue = new WorkQueueAdaptor();
        responseHandler = ResponseHandlerPipelineFactory.defaultPipeline();
    }

    public void start() {
        while (true) {
            try {
                WorkResponse response = responseQueue.nextResponse();
                responseHandler.handle(response);
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
    }
}
