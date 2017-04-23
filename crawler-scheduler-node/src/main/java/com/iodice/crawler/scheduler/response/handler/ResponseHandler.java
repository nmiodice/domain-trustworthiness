package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;

public interface ResponseHandler {
    /**
     * @return the input, or if it was changed, a copy of the response modified to reflect the handler processing
     *
     * @throws RuntimeException if any of the following occur:
     *  1. {@link WorkResponse} parameter is null
     *  2. {@link WorkResponse#getSource()} returns null or empty value
     *  3. {@link WorkResponse#getDestinations()} returns null or empty value
     *  4. {@link WorkResponse#getDestinations()} contains null or empty values
     */
    WorkResponse handle(WorkResponse response);
}
