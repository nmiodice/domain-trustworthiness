package com.iodice.webserver.http;

import com.iodice.config.Config;
import com.iodice.persistence.PageRankStore;
import com.iodice.webserver.http.response.PageRankBadRequestResponse;
import com.iodice.webserver.http.response.PageRankFoundResponse;
import com.iodice.webserver.http.response.PageRankMetadata;
import com.iodice.webserver.http.response.PageRankNotFoundResponse;
import com.iodice.webserver.http.response.PageRankResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Deque;
import java.util.Map;

public class PageRankRequestHandler implements HttpHandler {
    private static final Log logger = LogFactory.getLog(PageRankRequestHandler.class);

    private static final String DOMAIN_QUERY_PARAM = Config.getString("server.query.params.domain");
    private PageRankStore pageRankStore = new PageRankStore();
    private PageRankSummaryStatsCache pageRankStatsCache = new PageRankSummaryStatsCache();

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        logger.info("got request for page rank");
        PageRankResponse response = getResponse(exchange);
        logger.info("response: " + response);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/json");
        exchange.setResponseCode(response.getHTTPStatusCode());
        exchange.getResponseSender().send(response.toJSON());
    }

    private PageRankResponse getResponse(HttpServerExchange exchange) {
        String domain = getDomain(exchange);
        Double pageRank;

        if (domain == null) {
            return new PageRankBadRequestResponse(DOMAIN_QUERY_PARAM + " cannot be missing");
        }

        pageRank = pageRankStore.fetch(domain);
        if (pageRank == null) {
            return new PageRankNotFoundResponse(domain);
        }

        return new PageRankFoundResponse(PageRankMetadata.builder()
            .actualPageRank(pageRank)
            .domain(domain)
            .maxPageRank(pageRankStatsCache.getMaxPageRank())
            .minPageRank(pageRankStatsCache.getMinPageRank())
            .build());
    }

    private String getDomain(HttpServerExchange exchange) {
        Map<String, Deque<String>> params = exchange.getQueryParameters();
        if (params.isEmpty() || params.get(DOMAIN_QUERY_PARAM).isEmpty()) {
            return null;
        }

        return params.get(DOMAIN_QUERY_PARAM).getFirst();
    }
}
