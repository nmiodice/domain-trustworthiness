package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import org.apache.commons.lang3.Validate;

/**
 * helper class to validate work input before delegating the processing to a subclass implementation
 */
public abstract class ValidatedHandler implements ResponseHandler {
    /**
     * @param response a guaranteed to be validated handler
     * @return same as {@link ResponseHandler#handle(WorkResponse)}
     */
    abstract WorkResponse validatedHandle(WorkResponse response);

    @Override
    public WorkResponse handle(WorkResponse response) {
        Validate.notNull(response);
        Validate.notEmpty(response.getSource());
        Validate.notNull(response.getDestinations());
        Validate.notEmpty(response.getDestinations());
        return validatedHandle(response);
    }
}
