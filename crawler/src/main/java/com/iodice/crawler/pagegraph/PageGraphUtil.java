package com.iodice.crawler.pagegraph;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

class PageGraphUtil {
    private static final String DOMAIN_TO_PAGE_ID_DB_NAME = "domain-to-page-id";
    private static final String PAGE_ID_TO_DOMAIN_DB_NAME = "page-id-to-domain";

    private static final DB db = DBMaker.memoryDB().make();
    private final ConcurrentMap<String, Integer> domainToPageID;
    private final Map<Integer, String> pageIDtoDomain;

    private final AtomicInteger nextID;

    PageGraphUtil() {
        nextID = new AtomicInteger(0);
        domainToPageID = db.hashMap(DOMAIN_TO_PAGE_ID_DB_NAME, Serializer.STRING, Serializer.INTEGER).createOrOpen();
        pageIDtoDomain = db.hashMap(PAGE_ID_TO_DOMAIN_DB_NAME, Serializer.INTEGER, Serializer.STRING).createOrOpen();
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

    Integer pageID(String d) {
        return domainToPageID.get(d);
    }
}
