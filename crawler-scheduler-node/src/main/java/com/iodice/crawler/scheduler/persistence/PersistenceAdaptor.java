package com.iodice.crawler.scheduler.persistence;

import com.iodice.crawler.scheduler.utils.URLFacade;
import com.mongodb.BasicDBList;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        db.createIndex(URL_GRAPH_COLLECTION, GRAPH_SOURCE_KEY);
        db.createIndex(DOMAIN_GRAPH_COLLECTION, GRAPH_SOURCE_KEY);
        logger.info("created indices for DB");
    }

    public Map<String, Boolean> seenURLS(Collection<String> urls) {
        BasicDBList orQueryConditions = urls.stream()
            .map(url -> new Document(GRAPH_SOURCE_KEY, url))
            .collect(Collectors.toCollection(BasicDBList::new));
        Document query = new Document("$or", orQueryConditions);

        List<Document> responses = db.get(URL_GRAPH_COLLECTION, query);
        Set<String> seenURLs = responses.stream()
            .map(doc -> doc.getString(GRAPH_SOURCE_KEY))
            .collect(Collectors.toSet());

        return urls.stream()
            .collect(Collectors.toMap(Function.identity(), seenURLs::contains));
    }

    public boolean seenURL(String url) {
        return !db.get(URL_GRAPH_COLLECTION, new Document(GRAPH_SOURCE_KEY, url))
            .isEmpty();
    }

    public void storeURLEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(URL_GRAPH_COLLECTION, entry);
    }

    public void storeDomainEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        db.put(DOMAIN_GRAPH_COLLECTION, entry);
    }

    public void enqueueURLS(Collection<String> urls) {
        List<Document> entries = urls.stream()
            .map(url -> {
                String domain = URLFacade.toDomain(url);
                return domain == null ? null : toURLQueueDocument(domain, url);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        db.put(WORK_QUEUE_COLLECTION, entries);
    }

    private Document toURLQueueDocument(String domain, String url) {
        return new Document(WORK_QUEUE_DOMAIN_KEY, domain).append(WORK_QUEUE_URL_KEY, url)
            .append(WORK_QUEUE_DATE_KEY, new Date());
    }

    public List<String> getNexQueuedDomains(int count) {
        try {
            Document sampleQuery = new Document("$sample", new Document("size", count));
            return db.aggregateAndDelete(WORK_QUEUE_COLLECTION, sampleQuery)
                .stream()
                .map(doc -> doc.getString(WORK_QUEUE_URL_KEY))
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return null;
        }
    }
}
