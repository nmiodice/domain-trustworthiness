package com.iodice.webserver.http.response;

import io.undertow.util.StatusCodes;
import lombok.ToString;

@ToString
public class PageRankNotFoundResponse extends PageRankResponse {
    public PageRankNotFoundResponse(String domain) {
        responseMap.put(ERROR_KEY, "page rank not found");
        responseMap.put(CAUSE_KEY, "no page rank for " + domain);
    }

    @Override
    public int getHTTPStatusCode() {
        return StatusCodes.NOT_FOUND;
    }
}