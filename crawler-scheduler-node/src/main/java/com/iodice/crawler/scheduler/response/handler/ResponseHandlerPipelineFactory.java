package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;

/**
 * A handler that strings together many sub-handlers. This is a default configuration suitable for crawling the web
 * and gathering interesting metadata while avoiding duplicate work
 */
public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline(PersistenceAdaptor persistence) {
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenURLsHandler(persistence))
            .handler(new URLGraphStorageHandler(persistence))
            .handler(new DomainGraphStorageHandler(persistence))
            .handler(new DomainCountStorageHandler(persistence))
            .handler(new WorkQueueStorageHandler(persistence))
            .build();
    }
}
