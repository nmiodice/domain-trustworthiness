package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.utils.URLFacade;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class DomainCountStorageHandler extends ValidatedHandler {
    private static final Logger logger = LoggerFactory.getLogger(DomainCountStorageHandler.class);

    private PersistenceAdaptor persistence;

    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        String sourceDomain = URLFacade.toDomain(response.getSource());
        if (sourceDomain == null) {
            logger.warn(String.format("url='%s' returned a null domain", response.getSource()));
            return response;
        }
        persistence.incrementDomainSeenCount(sourceDomain);
        return response;
    }
}
