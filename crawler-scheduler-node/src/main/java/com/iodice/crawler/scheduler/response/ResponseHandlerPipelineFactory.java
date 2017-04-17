package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.response.handlers.FilterSeenHandler;
import com.iodice.crawler.scheduler.response.handlers.LinkGraphStorageHandler;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline() {
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenHandler())
            .handler(new LinkGraphStorageHandler())
            .build();
    }
}
