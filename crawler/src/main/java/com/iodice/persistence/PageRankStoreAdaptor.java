package com.iodice.persistence;

import com.iodice.config.Config;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PageRankStoreAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(PageRankStoreAdaptor.class);
    private static final String DB_HOST = Config.getString("db.meta.host");
    private static final String DB_NAME = Config.getString("db.meta.db_name");
    private static final String PAGE_RANK_COLLECTION_NAME = Config.getString("db.meta.page_rank_collection_name");

    private static final String DOMAIN_KEY = Config.getString("db.keys.domain");
    private static final String PAGE_RANK_KEY = Config.getString("db.keys.page_rank");

    private MongoClient mongo;

    PageRankStoreAdaptor() {
        mongo = new MongoClient(DB_HOST);
    }

    MongoCollection<Document> getCollection() {
        MongoDatabase db = mongo.getDatabase(DB_NAME);
        return db.getCollection(PAGE_RANK_COLLECTION_NAME);
    }

    void store(Map<String, Double> pageRanks) {
        if (pageRanks.size() == 0) {
            logger.info("skipping store of empty map");
            return;
        }
        List<Document> ranks = pageRanks.entrySet().stream().map(this::mapEntryToDocument).collect(Collectors.toList());

        logger.info("attempting to store " + ranks.size() + " items into db");
        getCollection().insertMany(ranks);
        logger.info("stored " + ranks.size() + " items into db");
    }

    private Document mapEntryToDocument(Map.Entry<String, Double> e) {
        return new Document(DOMAIN_KEY, e.getKey()).append(PAGE_RANK_KEY, e.getValue());
    }

    void deleteAll() {
        getCollection().deleteMany(new BasicDBObject());
    }
}
