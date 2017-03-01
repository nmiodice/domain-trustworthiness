package com.iodice.webserver;

import com.iodice.config.Config;
import io.undertow.Undertow;
import io.undertow.util.Headers;

public class Application {

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
            .addHttpListener(Config.getInt("server.conn.port"), Config.getString("server.conn.host"))
            .setHandler(new PageRankRequestHandler()).build();
        server.start();
    }
}

