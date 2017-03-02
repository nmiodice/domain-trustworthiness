package com.iodice.webserver;

import com.iodice.config.Config;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Application {
    private static final Log logger = LogFactory.getLog(Application.class);
    private static final String PAGE_RANK_PATH = "/pagerank";

    public static void main(final String[] args) {
        logger.info("building HTTP server");
        PathHandler handler = new PathHandler();
        handler.addPrefixPath(PAGE_RANK_PATH, new PageRankRequestHandler());

        String host = Config.getString("server.conn.host");
        Integer port = Config.getInt("server.conn.port");

        Undertow server = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(handler)
            .build();

        logger.info("starting HTTP server on " + host + ":" + port);
        server.start();
    }
}

