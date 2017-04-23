package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline(PersistenceAdaptor persistence) {
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenHandler(persistence))
            .handler(new URLGraphStorageHandler(persistence))
            .handler(new DomainGraphStorageHandler(persistence))
            .handler(new WorkQueueStorageHandler(persistence))
            .build();
    }
}
