package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.response.handlers.DomainGraphStorageHandler;
import com.iodice.crawler.scheduler.response.handlers.WorkQueueStorageHandler;
import com.iodice.crawler.scheduler.response.handlers.FilterSeenHandler;
import com.iodice.crawler.scheduler.response.handlers.URLGraphStorageHandler;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline() {
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenHandler())
            .handler(new URLGraphStorageHandler())
            .handler(new DomainGraphStorageHandler())
            .handler(new WorkQueueStorageHandler())
            .build();
    }
}
