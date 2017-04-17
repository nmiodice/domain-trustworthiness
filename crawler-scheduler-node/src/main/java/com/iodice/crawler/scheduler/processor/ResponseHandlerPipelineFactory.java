package com.iodice.crawler.scheduler.processor;

import com.iodice.crawler.scheduler.processor.handlers.FilterSeenHandler;
import com.iodice.crawler.scheduler.processor.handlers.LinkGraphStorageHandler;
import com.iodice.crawler.scheduler.processor.handlers.WorkDispatchHandler;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline() {
        return ResponseHandlerPipeline.builder()
            .handler(new FilterSeenHandler())
            .handler(new LinkGraphStorageHandler())
            .handler(new WorkDispatchHandler())
            .build();
    }
}
