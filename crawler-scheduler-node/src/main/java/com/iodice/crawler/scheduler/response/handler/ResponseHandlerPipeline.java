package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A handler that dispatches work to other handlers in a chain. The pipeline will exit early if there is no more work
 * to do, to avoid sending empty work to subsequent handlers in the chain
 */
@Builder
@AllArgsConstructor
public class ResponseHandlerPipeline extends ValidatedHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandlerPipeline.class);

    @Singular
    @Getter
    private List<ResponseHandler> handlers;

    public WorkResponse validatedHandle(WorkResponse response) {
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
