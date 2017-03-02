package com.iodice.webserver.http.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public abstract class PageRankResponse {
    static final String ERROR_KEY = "error";
    static final String CAUSE_KEY = "cause";
    static final String DOMAIN_KEY = "domain";
    static final String PAGE_RANK_KEY = "pagerank";
    static final String PAGE_RANK_MAX_KEY = "max_page_rank";
    static final String PAGE_RANK_MIN_KEY = "min_page_rank";

    Map<String, Object> responseMap = new HashMap<>();

    public abstract int getHTTPStatusCode();

    @SneakyThrows
    public String toJSON() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(responseMap);
    }
}
