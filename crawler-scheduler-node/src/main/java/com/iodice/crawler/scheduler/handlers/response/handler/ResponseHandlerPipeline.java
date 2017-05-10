package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A handler that dispatches work to other handlers in a chain. The handlers will exit early if there is no more work
 * to do, to avoid sending empty work to subsequent handlers in the chain
 */
@Builder
@AllArgsConstructor
public class ResponseHandlerPipeline extends ValidatedResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandlerPipeline.class);

    @Singular
    @Getter
    private List<PayloadHandler<WorkResponse>> handlers;

    public WorkResponse validatedHandle(WorkResponse response) {
        for (PayloadHandler<WorkResponse> handler : handlers) {
            if (response == null) {
                logger.info("exiting handler handlers early because a handler returned a null response.");
                return null;
            }
            if (response.getDestinations()
                .isEmpty()) {
                logger.info("exiting handler handlers early because there are no more outbound links");
                return response;
            }
            response = handler.handle(response);
        }
        logger.info("done handling worker response");
        return response;
    }

}
