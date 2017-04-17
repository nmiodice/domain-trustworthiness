package com.iodice.crawler.scheduler.processor;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.processor.handlers.ResponseHandler;
import lombok.Builder;
import lombok.Singular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Builder
public class ResponseHandlerPipeline {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandlerPipeline.class);

    @Singular
    private List<ResponseHandler> handlers;

    public void handle(WorkResponse response) {
        logger.info(String.format("handling URL='%s' with %d destinations", response.getSource(),
            response.getDestinations()
                .size()));
        for (ResponseHandler handler : handlers) {
            if (response.getDestinations()
                .isEmpty()) {
                logger.info("exiting handler pipeline early because there are no more outbound links");
                return;
            }
            response = handler.handle(response);
        }
    }

}
