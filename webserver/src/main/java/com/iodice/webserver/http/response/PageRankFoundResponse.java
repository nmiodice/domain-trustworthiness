package com.iodice.webserver.http.response;

import io.undertow.util.StatusCodes;
import lombok.ToString;

@ToString
public class PageRankFoundResponse extends PageRankResponse {
    public PageRankFoundResponse(PageRankMetadata metadata) {
        responseMap.put(DOMAIN_KEY, metadata.getDomain());
        responseMap.put(PAGE_RANK_KEY, metadata.getActualPageRank());
        responseMap.put(PAGE_RANK_MAX_KEY, metadata.getMaxPageRank());
        responseMap.put(PAGE_RANK_MIN_KEY, metadata.getMinPageRank());
    }

    @Override
    public int getHTTPStatusCode() {
        return StatusCodes.OK;
    }
}