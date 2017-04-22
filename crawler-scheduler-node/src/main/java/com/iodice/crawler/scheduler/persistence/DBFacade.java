package com.iodice.crawler.scheduler.persistence;

import com.iodice.config.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class DBFacade {
    private static final Logger logger = LoggerFactory.getLogger(DBFacade.class);
    private static final String DB_HOST = Config.getString("db.meta.host");
    private static final String DB_NAME = Config.getString("db.meta.db_name");

    private static final MongoClient mongo;

    static {
        mongo = new MongoClient(DB_HOST);
        logger.info("MongoDB client initialized");
    }

    void createIndex(String collection, String column) {
        getCollection(collection).createIndex(Indexes.ascending(column));
    }

    void put(String collection, Document item) {
        put(collection, Collections.singletonList(item));
    }

    void put(String collection, List<Document> items) {
        getCollection(collection).insertMany(items);
    }

    List<Document> get(String collection, Document query) {
        List<Document> results = new ArrayList<>();
        for (Document document : getCollection(collection).find(query)) {
            results.add(document);
        }

        return results;
    }

    List<Document> aggregateAndDelete(String collection, Document... clauses) {
        // these are documents in an aggregated form and therefore the key (_id) may not
        // be valid when we try to delete. so it is important to transform them into something deletable
        // by removing the key
        List<Document> results = aggregate(collection, clauses);
        for (Document d : results) {
            d.remove("_id");
        }

        delete(collection, results);
        return results;
    }

    List<Document> aggregate(String collection, Document... clauses) {
        AggregateIterable<Document> iterator = getCollection(collection).aggregate(Arrays.asList(clauses));
        return toList(iterator);
    }

    private void delete(String collection, Collection<Document> items) {
        if (items.isEmpty()) {
            return;
        }
        getCollection(collection).deleteMany(new Document("$or", items));
    }

    private MongoCollection<Document> getCollection(String collection) {
        return getCollection(collection, DB_NAME);
    }

    /**
     * Used only for integration testing
     */
    MongoCollection<Document> getCollection(String collection, String dbName) {
        MongoDatabase db = mongo.getDatabase(dbName);
        return db.getCollection(collection);
    }

    private <T> List<T> toList(Iterable<T> iterator) {
        return StreamSupport
            .stream(iterator.spliterator(), false)
            .collect(Collectors.toList());
    }
}
