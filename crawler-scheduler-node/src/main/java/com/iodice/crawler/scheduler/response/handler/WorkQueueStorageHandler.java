package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import lombok.AllArgsConstructor;

/**
 * Stores URLs in a {@link WorkResponse} in domain queues that are eligible for crawling
 */
@AllArgsConstructor
class WorkQueueStorageHandler extends ValidatedHandler {
    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        persistence.enqueueURLS(response.getDestinations());
        return response;
    }
}
