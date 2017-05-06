package com.iodice.crawler.scheduler.handlers.request.handler;

import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import org.apache.commons.lang3.Validate;

abstract class ValidatedRequestHandler implements PayloadHandler<WorkRequest> {
    abstract WorkRequest validatedHandle(WorkRequest request);

    /**
     * @return the input, or if it was changed, a copy of the request modified to reflect the handler processing
     * @throws RuntimeException if any of the following occur:
     *                          1. {@link WorkRequest} parameter is null
     *                          2. {@link WorkRequest#getUrls()} ()} returns null or empty value
     *                          4. {@link WorkRequest#getUrls()} contains null or empty values
     */
    @Override
    public WorkRequest handle(WorkRequest request) {
        Validate.notNull(request);
        Validate.notEmpty(request.getUrls());

        for (String s : request.getUrls()) {
            Validate.notBlank(s);
        }
        return validatedHandle(request);
    }
}
