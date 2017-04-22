package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.response.handlers.ResponseHandler;
import lombok.Builder;
import lombok.Singular;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Builder
class ResponseHandlerPipeline implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandlerPipeline.class);

    @Singular
    private List<ResponseHandler> handlers;

    public WorkResponse handle(WorkResponse response) {
        Validate.notNull(response);
        Validate.notEmpty(response.getSource());
        Validate.notEmpty(response.getDestinations());
        Validate.noNullElements(response.getDestinations());

        logger.info(String.format("handling URL='%s' with %d destinations", response.getSource(),
            response.getDestinations()
                .size()));

        for (ResponseHandler handler : handlers) {
            if (response == null) {
                logger.info("exiting handler pipeline early because a handler returned a null response.");
                return response;
            }
            if (response.getDestinations()
                .isEmpty()) {
                logger.info("exiting handler pipeline early because there are no more outbound links");
                return response;
            }
            response = handler.handle(response);
        }
        return response;
    }

}
