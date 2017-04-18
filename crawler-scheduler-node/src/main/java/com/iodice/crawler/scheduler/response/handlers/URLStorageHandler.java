package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;

public class URLStorageHandler implements ResponseHandler {
    private PersistenceAdaptor persistence = new PersistenceAdaptor();

    @Override
    public WorkResponse handle(WorkResponse response) {
        persistence.storeURLEdges(response.getSource(), response.getDestinations());
        return response;
    }
}
