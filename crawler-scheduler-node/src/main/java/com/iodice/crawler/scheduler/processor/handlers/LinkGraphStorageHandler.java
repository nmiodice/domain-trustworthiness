package com.iodice.crawler.scheduler.processor.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;


public class LinkGraphStorageHandler implements ResponseHandler {
    private PersistenceAdaptor persistence = new PersistenceAdaptor();

    @Override
    public WorkResponse handle(WorkResponse response) {
        persistence.storeLinks(response.getSource(), response.getDestinations());
        return response;
    }
}
