package com.iodice.crawler.scheduler.processor;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public class ResponseHandlerPipeline {
    @Singular
    private List<ResponseHandler> handlers;

    public void handle(WorkResponse response) {
        for (ResponseHandler handler : handlers) {
            response = handler.handle(response);
        }
    }

}
