package com.iodice.crawler.scheduler.response.handler;

import com.iodice.config.Config;
import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.utils.URLFacade;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Stores URLs in a {@link WorkResponse} in domain queues that are eligible for crawling
 */
@AllArgsConstructor
class WorkQueueStorageHandler extends ValidatedHandler {
    private static final Logger logger = LoggerFactory.getLogger(WorkQueueStorageHandler.class);
    private final static int MAX_SEEN_COUNT = Config.getInt("filters.max_domain_seen_count");

    private PersistenceAdaptor persistence;


    @Override
    public WorkResponse validatedHandle(WorkResponse response) {
        Collection<String> validWorkItems = filterHighSeenDomains(response.getDestinations());
        if (!validWorkItems.isEmpty()) {
            persistence.enqueueURLS(validWorkItems);
        }
        return response;
    }

    private Collection<String> filterHighSeenDomains(Collection<String> urls) {
        return urls.stream()
            .filter(url -> seenCount(URLFacade.toDomain(url)) < MAX_SEEN_COUNT)
            .collect(Collectors.toList());
    }

    private int seenCount(String domain) {
        try {
            return persistence.getDomainSeenCount(domain);
        } catch (Exception e) {
            logger.error(String.format("error determining domain count for domain='%s'", domain));
            return 0;
        }
    }
}
