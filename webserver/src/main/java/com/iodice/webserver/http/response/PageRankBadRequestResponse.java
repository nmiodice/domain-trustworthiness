package com.iodice.webserver.http.response;

import io.undertow.util.StatusCodes;
import lombok.ToString;

@ToString
public class PageRankBadRequestResponse extends PageRankResponse {

    public PageRankBadRequestResponse(String cause) {
        responseMap.put(ERROR_KEY, "bad request");
        responseMap.put(CAUSE_KEY, cause);
    }

    @Override
    public int getHTTPStatusCode() {
        return StatusCodes.BAD_REQUEST;
    }
}
