package com.iodice.webserver;

import com.iodice.config.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.Deque;
import java.util.Map;


public class PageRankRequestHandler implements HttpHandler {
    private static final Log logger = LogFactory.getLog(PageRankRequestHandler.class);

    private static final String DOMAIN_HEADER = Config.getString("server.header.domain");
    private static final String DB_HOST = Config.getString("db.meta.host");
    private static final String DB_NAME = Config.getString("db.meta.db_name");
    private static final String COLLECTION_NAME = Config.getString("db.meta.page_rank_collection_name");
    private static final String DOMAIN_KEY = Config.getString("db.keys.domain");
    private static final String PAGE_RANK_KEY = Config.getString("db.keys.page_rank");

    private MongoDatabase db = new MongoClient(DB_HOST).getDatabase(DB_NAME);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        if (isValidRequest(exchange)) {
            respondToValidRequest(exchange);
        } else {
            respondToInvalidRequest(exchange);
        }
    }

    private boolean isValidRequest(HttpServerExchange exchange) {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        return !params.isEmpty() && !params.get(DOMAIN_HEADER).isEmpty();
    }

    private void respondToInvalidRequest(HttpServerExchange exchange) {
        logger.info("handling invalid request");
        exchange.setResponseCode(StatusCodes.BAD_REQUEST);
        exchange.getResponseSender().send(DOMAIN_HEADER + " cannot be missing");
    }

    private void respondToValidRequest(HttpServerExchange exchange) {
        String domain = exchange.getQueryParameters().get(DOMAIN_HEADER).getFirst();
        logger.info("handling invalid request: domain = " + domain);
        respondToSingleDomainRequest(exchange, domain);
    }

    private void respondToSingleDomainRequest(HttpServerExchange exchange, String domain) {
        Document result = db.getCollection(COLLECTION_NAME).find(new Document(DOMAIN_KEY, domain)).first();
        if (result == null) {
            logger.info("no page rank found: domain = " + domain);
            exchange.setResponseCode(StatusCodes.NOT_FOUND);
        } else {
            Double pageRank = result.getDouble(PAGE_RANK_KEY);
            logger.info("page rank found: domain = " + domain + ", page rank = " + pageRank);
            exchange.setResponseCode(StatusCodes.OK);
            exchange.getResponseSender().send(pageRank.toString());
        }
    }
}
