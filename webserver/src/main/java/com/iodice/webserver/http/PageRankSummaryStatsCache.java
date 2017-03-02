package com.iodice.webserver.http;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iodice.persistence.PageRankStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

class PageRankSummaryStatsCache {
    private static final Log logger = LogFactory.getLog(PageRankSummaryStatsCache.class);
    private PageRankStore store = new PageRankStore();
    private LoadingCache<STAT_TYPES, Double> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<STAT_TYPES, Double>() {
            public Double load(STAT_TYPES key) {
                logger.info("cache miss for " + key);

                switch (key) {
                case MAX:
                    return store.getMaxPageRank();
                case MIN:
                    return store.getMinPageRank();
                default:
                    throw new IllegalStateException("unknown key");
                }
            }
        });

    Double getMaxPageRank() {
        try {
            return cache.get(STAT_TYPES.MAX);
        } catch (Exception e) {
            logger.error("cache error: " + e.getMessage(), e);
            return store.getMaxPageRank();
        }
    }

    Double getMinPageRank() {
        try {
            return cache.get(STAT_TYPES.MIN);
        } catch (Exception e) {
            logger.error("cache error: " + e.getMessage(), e);
            return store.getMinPageRank();
        }
    }

    private enum STAT_TYPES {
        MIN, MAX
    }
}
