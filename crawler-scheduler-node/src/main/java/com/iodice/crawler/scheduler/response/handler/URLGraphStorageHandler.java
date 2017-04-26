package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import lombok.AllArgsConstructor;

/**
 * stores a graph with nodes like:
 *  url -> [url]
 */
@AllArgsConstructor
class URLGraphStorageHandler extends ValidatedHandler {
    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        persistence.storeURLEdges(response.getSource(), response.getDestinations());
        return response;
    }
}
