package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import org.apache.commons.lang3.Validate;

abstract class ValidatedResponseHandler implements PayloadHandler<WorkResponse> {
    /**
     * @param response a guaranteed to be validated handler
     * @return same as {@link #handle(WorkResponse)}
     */
    abstract WorkResponse validatedHandle(WorkResponse response);

    /**
     * @return the input, or if it was changed, a copy of the response modified to reflect the handler processing
     * @throws RuntimeException if any of the following occur:
     *                          1. {@link WorkResponse} parameter is null
     *                          2. {@link WorkResponse#getSource()} returns null or empty value
     *                          3. {@link WorkResponse#getDestinations()} returns null or empty value
     *                          4. {@link WorkResponse#getDestinations()} contains null or empty values
     */
    @Override
    public WorkResponse handle(WorkResponse response) {
        Validate.notNull(response);
        Validate.notBlank(response.getSource());
        Validate.notNull(response.getDestinations());
        Validate.notEmpty(response.getDestinations());

        for (String s : response.getDestinations()) {
            Validate.notBlank(s);
        }
        return validatedHandle(response);
    }
}
