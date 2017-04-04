package com.iodice.crawler.pagegraph;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

class PageGraphUtil {

    private static final DB db = DBMaker.memoryDB()
        .make();
    private final ConcurrentMap<String, Integer> domainToPageID;
    private final Map<Integer, String> pageIDtoDomain;

    private final AtomicInteger nextID;

    PageGraphUtil() {
        nextID = new AtomicInteger(0);
        domainToPageID = db.hashMap(UUID.randomUUID()
            .toString(), Serializer.STRING, Serializer.INTEGER);
        pageIDtoDomain = db.hashMap(UUID.randomUUID()
            .toString(), Serializer.INTEGER, Serializer.STRING);
    }

    Integer toPageID(String domain) {
        synchronized (domainToPageID) {
            if (!domainToPageID.containsKey(domain)) {
                Integer id = nextID.getAndIncrement();
                domainToPageID.put(domain, id);
                pageIDtoDomain.put(id, domain);
            }
            return domainToPageID.get(domain);
        }
    }

    String domain(Integer i) {
        return pageIDtoDomain.get(i);
    }
}
