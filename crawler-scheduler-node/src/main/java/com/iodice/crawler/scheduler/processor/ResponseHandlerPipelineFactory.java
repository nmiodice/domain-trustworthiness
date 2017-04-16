package com.iodice.crawler.scheduler.processor;

public class ResponseHandlerPipelineFactory {
    public static ResponseHandlerPipeline defaultPipeline() {
        return ResponseHandlerPipeline.builder()
            .handler(new WorkDispatchHandler())
            .build();
    }
}
