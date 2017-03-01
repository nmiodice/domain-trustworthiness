package com.iodice.queue;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PageRankJobParams {
    @NonNull
    Integer runtime;
}
