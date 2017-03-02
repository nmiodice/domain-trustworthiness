package com.iodice.persistence;

import com.iodice.config.Config;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageRankStore {
    private static final Logger logger = LoggerFactory.getLogger(PageRankStore.class);
    private static final String DB_HOST = Config.getString("db.meta.host");
    private static final String DB_NAME = Config.getString("db.meta.db_name");
    private static final String PAGE_RANK_COLLECTION_NAME = Config.getString("db.meta.page_rank_collection_name");

    private static final String DOMAIN_KEY = Config.getString("db.keys.domain");
    private static final String PAGE_RANK_KEY = Config.getString("db.keys.page_rank");

    private MongoClient mongo;

    public PageRankStore() {
        mongo = new MongoClient(DB_HOST);
    }

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = mongo.getDatabase(DB_NAME);
        return db.getCollection(PAGE_RANK_COLLECTION_NAME);
    }

    public void store(Map<String, Double> pageRanks) {
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

    public void deleteAll() {
        DeleteResult result = getCollection().deleteMany(new BasicDBObject());
        logger.info("deleted " + result.getDeletedCount() + " entries in DB");
    }

    public Double fetch(String domain) {
        Document result = getCollection().find(new Document(DOMAIN_KEY, domain)).first();
        if (result == null) {
            logger.info("no page rank found: domain = " + domain);
            return null;
        }
        Double pageRank = result.getDouble(PAGE_RANK_KEY);
        logger.info("page rank found: domain = " + domain + ", page rank = " + pageRank);
        return pageRank;
    }

    public Double getMinPageRank() {
        Document result = getCollection().find(new Document()).sort(new BasicDBObject(PAGE_RANK_KEY, 1)).first();
        if (result == null) {
            return null;
        }

        return result.getDouble(PAGE_RANK_KEY);
    }

    public Double getMaxPageRank() {
        Document result = getCollection().find(new Document()).sort(new BasicDBObject(PAGE_RANK_KEY, -1)).first();
        if (result == null) {
            return null;
        }

        return result.getDouble(PAGE_RANK_KEY);
    }
}
