package com.iodice.crawler.scheduler.utils;

import io.mola.galimatias.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLFacade {
    private static final Logger logger = LoggerFactory.getLogger(URLFacade.class);

    public static String toDomain(String url) {
        try {
            return URL.parse(url)
                .host()
                .toString();
        } catch (Exception e) {
            logger.error(String.format("unable to get domain for URL ='%s'", url), e);
            return null;
        }
    }
}
