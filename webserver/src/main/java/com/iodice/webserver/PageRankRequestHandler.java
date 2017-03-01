package com.iodice.webserver;

import com.iodice.config.Config;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.bson.Document;

import javax.print.Doc;
import java.util.Deque;
import java.util.Map;


public class PageRankRequestHandler implements HttpHandler {
    private static final String DOMAIN_HEADER = "domain";
    private static final String DB_HOST = Config.getString("db.meta.host");
    private static final String DB_NAME = Config.getString("db.meta.db_name");
    private static final String COLLECTION_NAME = Config.getString("db.meta.page_rank_collection_name");
    private static final String DOMAIN_KEY = Config.getString("db.keys.domain");
    private static final String PAGE_RANK_KEY = Config.getString("db.keys.page_rank");

    MongoDatabase db = new MongoClient(DB_HOST).getDatabase(DB_NAME);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");

        if (params.isEmpty() || params.get(DOMAIN_HEADER).isEmpty()) {
            exchange.setResponseCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send(DOMAIN_HEADER + " cannot be missing");
        } else {
            String domain = params.get(DOMAIN_HEADER).getFirst();
            Document result = db.getCollection(COLLECTION_NAME).find(new Document(DOMAIN_KEY, domain)).first();

            if (result == null) {
                exchange.setResponseCode(StatusCodes.NOT_FOUND);
            } else {
                exchange.setResponseCode(StatusCodes.OK);
                exchange.getResponseSender().send(result.getDouble(PAGE_RANK_KEY).toString());
            }
        }

    }
}
