package com.iodice.crawler.scheduler.handlers.request.handler;

import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@AllArgsConstructor
@Builder
class RequestHandlerPipeline extends ValidatedRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerPipeline.class);

    @Singular
    @Getter
    private List<PayloadHandler<WorkRequest>> handlers;

    @Override
    WorkRequest validatedHandle(WorkRequest request) {
        logger.info(String.format("handling request for %d urls", request.getUrls()
            .size()));

        for (PayloadHandler<WorkRequest> handler : handlers) {
            if (request == null) {
                logger.info("exiting handler handlers early because a handler returned a null request.");
                return null;
            }
            if (request.getUrls()
                .isEmpty()) {
                logger.info("exiting handler handlers early because there are no URLs to process");
                return request;
            }
            request = handler.handle(request);
        }
        return request;
    }
}
