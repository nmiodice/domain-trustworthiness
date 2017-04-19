package com.iodice.crawler.scheduler.persistence;

import com.iodice.config.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DBFacade {
    private static final Logger logger = LoggerFactory.getLogger(DBFacade.class);
    private static final String DB_HOST = Config.getString("db.meta.host");
    private static final String DB_NAME = Config.getString("db.meta.db_name");

    private static final MongoClient mongo;

    static {
        mongo = new MongoClient(DB_HOST);
        logger.info("MongoDB client initialized");
    }

    private MongoCollection<Document> getCollection(String collection) {
        MongoDatabase db = mongo.getDatabase(DB_NAME);
        return db.getCollection(collection);
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

    void createIndex(String collection, String column) {
        getCollection(collection).createIndex(Indexes.ascending(column));
    }
}
