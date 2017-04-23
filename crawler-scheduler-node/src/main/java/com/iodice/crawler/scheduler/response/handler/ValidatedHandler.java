package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import org.apache.commons.lang3.Validate;

/**
 * helper class to validate work input before delegating the processing to a subclass implementation. This ensures
 * that the validation specified by {@link ResponseHandler} is done for all classes that inherit from this class
 */
abstract class ValidatedHandler implements ResponseHandler {
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
        Validate.noNullElements(response.getDestinations());
        return validatedHandle(response);
    }
}
