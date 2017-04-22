package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.response.handlers.DomainGraphStorageHandler;
import com.iodice.crawler.scheduler.response.handlers.WorkQueueStorageHandler;
import com.iodice.crawler.scheduler.response.handlers.FilterSeenHandler;
import com.iodice.crawler.scheduler.response.handlers.URLGraphStorageHandler;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline() {
        PersistenceAdaptor persistence = new PersistenceAdaptor();
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenHandler(persistence))
            .handler(new URLGraphStorageHandler(persistence))
            .handler(new DomainGraphStorageHandler(persistence))
            .handler(new WorkQueueStorageHandler(persistence))
            .build();
    }
}
