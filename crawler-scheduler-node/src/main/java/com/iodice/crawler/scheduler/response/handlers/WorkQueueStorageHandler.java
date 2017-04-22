package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorkQueueStorageHandler implements ResponseHandler {
    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse handle(WorkResponse response) {
        persistence.enqueueURLS(response.getDestinations());
        return response;
    }
}
