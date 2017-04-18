package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.response.handlers.DomainStorageHandler;
import com.iodice.crawler.scheduler.response.handlers.FilterSeenHandler;
import com.iodice.crawler.scheduler.response.handlers.URLStorageHandler;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline() {
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenHandler())
            .handler(new URLStorageHandler())
            .handler(new DomainStorageHandler())
            .build();
    }
}
