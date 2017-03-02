package com.iodice.webserver.http.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PageRankMetadata {
    private double maxPageRank;
    private double minPageRank;
    private double actualPageRank;
    private String domain;
}
