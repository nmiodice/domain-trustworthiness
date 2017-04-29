package com.iodice.crawler.scheduler.persistence;

import com.iodice.crawler.scheduler.utils.URLFacade;
import com.mongodb.BasicDBList;
import com.mongodb.MongoBulkWriteException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class PersistenceAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceAdaptor.class);

    private static final String URL_GRAPH_COLLECTION = "UrlGraph";
    private static final String DOMAIN_GRAPH_COLLECTION = "DomainGraph";
    private static final String GRAPH_SOURCE_KEY = "_id";
    private static final String GRAPH_DESTINATIONS_KEY = "destinations";

    private static final String WORK_QUEUE_COLLECTION = "DomainQueues";
    private static final String WORK_QUEUE_DOMAIN_KEY = "domain";
    private static final String WORK_QUEUE_URL_KEY = "url";
    private static final String WORK_QUEUE_DATE_KEY = "time";

    private static final String DOMAIN_SEEN_COUNT_COLLECTION = "DomainCount";
    private static final String DOMAIN_SEEN_ID_KEY = "_id";
    private static final String DOMAIN_SEEN_COUNT_KEY = "seenCount";


    private final DBFacade db;

    public PersistenceAdaptor() {
        this(new DBFacade());
    }

    private PersistenceAdaptor(DBFacade db) {
        this.db = db;
        initDB();
    }

    private void initDB() {
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
        return seenURLS(Collections.singleton(url)).get(url);
    }

    public void storeURLEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        store(URL_GRAPH_COLLECTION, entry);
    }

    public void storeDomainEdges(String source, Collection<String> destinations) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source).append(GRAPH_DESTINATIONS_KEY, destinations);
        store(DOMAIN_GRAPH_COLLECTION, entry);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getURLEdges(String source) {
        Document entry = new Document(GRAPH_SOURCE_KEY, source);
        List<Document> results = db.get(URL_GRAPH_COLLECTION, entry);
        return results.stream()
            .map(document -> (List<String>) document.get(GRAPH_DESTINATIONS_KEY))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<String> getNexQueuedDomains(int count) {
        try {
            Document[] queries = new Document[] {
                domainQueueSortQuery(), domainQueueGroupQuery(), domainQueueSampleQuery(count)
            };
            return db.aggregateAndDelete(WORK_QUEUE_COLLECTION, queries)
                .stream()
                .map(doc -> doc.getString(WORK_QUEUE_URL_KEY))
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return null;
        }
    }

    public void incrementDomainSeenCount(String domain) {
        Bson filter = eq(DOMAIN_SEEN_ID_KEY, domain);
        Document update = new Document("$inc", new Document(DOMAIN_SEEN_COUNT_KEY, 1));
        db.update(DOMAIN_SEEN_COUNT_COLLECTION, filter, update);
    }

    public int getDomainSeenCount(String domain) {
        List<Document> results = db.get(DOMAIN_SEEN_COUNT_COLLECTION, new Document(DOMAIN_SEEN_ID_KEY, domain));
        if (results.isEmpty()) {
            return 0;
        }

        return results.get(0).getInteger(DOMAIN_SEEN_COUNT_KEY);
    }

    private Document domainQueueSortQuery() {
        return new Document("$sort", new Document(WORK_QUEUE_DOMAIN_KEY, 1).append(WORK_QUEUE_DATE_KEY, 1));
    }

    private Document domainQueueGroupQuery() {
        Document clauses = new Document("_id", dollar(WORK_QUEUE_DOMAIN_KEY)).append(WORK_QUEUE_DOMAIN_KEY,
            dollarFirst(WORK_QUEUE_DOMAIN_KEY))
            .append(WORK_QUEUE_DATE_KEY, dollarFirst(WORK_QUEUE_DATE_KEY))
            .append(WORK_QUEUE_URL_KEY, dollarFirst(WORK_QUEUE_URL_KEY));

        return new Document(dollar("group"), clauses);
    }

    private Document domainQueueSampleQuery(int sampleSize) {
        return new Document(dollar("sample"), new Document("size", sampleSize));
    }

    private Document dollarFirst(String key) {
        return new Document(dollar("first"), dollar(key));
    }

    private String dollar(String s) {
        return "$" + s;
    }

    public void enqueueURLS(Collection<String> urls) {
        List<Document> entries = urls.stream()
            .map(url -> {
                String domain = URLFacade.toDomain(url);
                return domain == null ? null : toURLQueueDocument(domain, url);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        store(WORK_QUEUE_COLLECTION, entries);
    }

    private Document toURLQueueDocument(String domain, String url) {
        return new Document(WORK_QUEUE_DOMAIN_KEY, domain).append(WORK_QUEUE_URL_KEY, url)
            .append(WORK_QUEUE_DATE_KEY, new Date());
    }

    private void store(String collection, Document document) {
        store(collection, Collections.singletonList(document));
    }

    private void store(String collection, List<Document> documents) {
        try {
            db.put(collection, documents);
        } catch (MongoBulkWriteException e) {
            logger.debug("ignoring duplicate key");
        }
    }
}
