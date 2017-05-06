package com.iodice.crawler.scheduler.handlers.request.handler;

import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;

/**
 * A handler that strings together many sub-handlers. This is a default configuration suitable for crawling the web
 * and gathering interesting metadata while avoiding duplicate work
 */
public class RequestHandlerPipelineFactory {
    public static PayloadHandler<WorkRequest> defaultPipeline(PersistenceAdaptor persistence,
        RequestQueueAdaptor requestQueue) {
        return RequestHandlerPipeline.builder()
            .handler(new RequestEmitterHandler(requestQueue))
            .build();
    }
}