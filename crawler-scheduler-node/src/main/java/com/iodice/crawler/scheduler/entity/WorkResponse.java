package com.iodice.crawler.scheduler.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class WorkResponse {
    private String source;
    private Collection<String> destinations;
}
