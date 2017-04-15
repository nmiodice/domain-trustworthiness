package edu.uci.ics.crawler4j.frontier;

import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Collection;
import java.util.List;

public interface Frontier {
    List<WebURL> getNextURLs();

    void scheduleAll(Collection<WebURL> destinations, WebURL source);

    void schedule(WebURL destination, WebURL source);

    boolean isShutdown();

    void shutdown();
}
