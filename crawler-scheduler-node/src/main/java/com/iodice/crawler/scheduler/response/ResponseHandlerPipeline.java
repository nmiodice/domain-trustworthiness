package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.response.handlers.ResponseHandler;
import lombok.Builder;
import lombok.Singular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Builder
class ResponseHandlerPipeline {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandlerPipeline.class);

    @Singular
    private List<ResponseHandler> handlers;

    void handle(WorkResponse response) {
        logger.info(String.format("handling URL='%s' with %d destinations", response.getSource(),
            response.getDestinations()
                .size()));

        for (ResponseHandler handler : handlers) {
            if (response == null) {
                logger.info("exiting handler pipeline early because a handler returned a null response.");
                return;
            }
            if (response.getDestinations()
                .isEmpty()) {
                logger.info("exiting handler pipeline early because there are no more outbound links");
                return;
            }
            response = handler.handle(response);
        }
    }

}
