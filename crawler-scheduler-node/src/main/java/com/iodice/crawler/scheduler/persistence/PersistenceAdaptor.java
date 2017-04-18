package com.iodice.crawler.scheduler.persistence;

import com.iodice.crawler.scheduler.utils.URLFacade;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;

public class PersistenceAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceAdaptor.class);

    private static final String URL_GRAPH_COLLECTION = "UrlGraph";
    private static final String DOMAIN_GRAPH_COLLECTION = "DomainGraph";
    private static final String GRAPH_SOURCE_KEY = "source";
    private static final String GRAPH_DESTINATIONS_KEY = "destinations";

    private static final String WORK_QUEUE_COLLECTION = "DomainQueues";
    private static final String WORK_QUEUE_DOMAIN_KEY = "domain";
    private static final String WORK_QUEUE_URL_KEY = "url";
    private static final String WORK_QUEUE_DATE_KEY = "time";

    private static final DBFacade db = new DBFacade();

    static {
        db.createIndex(GRAPH_SOURCE_KEY, URL_GRAPH_COLLECTION);
        db.createIndex(GRAPH_SOURCE_KEY, DOMAIN_GRAPH_COLLECTION);
        logger.info("created indices for DB");
    }

    public boolean seenURL(String source) {
        return !db.get(new Document(GRAPH_SOURCE_KEY, source), URL_GRAPH_COLLECTION)
            .isEmpty();
    }

    public void storeURLEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(entry, URL_GRAPH_COLLECTION);
    }

    public void storeDomainEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(entry, DOMAIN_GRAPH_COLLECTION);
    }

    public void enqueueURL(String url) {
        String domain = URLFacade.toDomain(url);
        if (StringUtils.isEmpty(domain)) {
            return;
        }

        Document entry = new Document(WORK_QUEUE_DOMAIN_KEY, domain).append(WORK_QUEUE_URL_KEY, url)
            .append(WORK_QUEUE_DATE_KEY, new Date());
        db.put(entry, WORK_QUEUE_COLLECTION);
    }
}
