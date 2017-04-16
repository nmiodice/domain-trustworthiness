package com.iodice.crawler.scheduler.processor;

import com.iodice.crawler.scheduler.entity.WorkResponse;

public interface ResponseHandler {

    /**
     * @return the input, or if it was changed, a copy of the response modified to reflect the handlers processing
     */
    WorkResponse handle(WorkResponse response);
}
