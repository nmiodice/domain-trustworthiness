package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class URLGraphStorageHandler extends ValidatedHandler {
    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        persistence.storeURLEdges(response.getSource(), response.getDestinations());
        return response;
    }
}
