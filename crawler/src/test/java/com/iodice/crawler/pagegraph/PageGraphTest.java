package com.iodice.crawler.pagegraph;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PageGraphTest {

    @Test
    public void collectAndRemoveDanglingPages_shouldRemoveLevelOneDanglerInOneIteration() {
        PageGraph pg = PageGraphFactory.memoryDBBackedPageGraph();
        try {
            pg.add("www.0.com", "www.1.com");
            pg.add("www.1.com", "www.2.com");

            PageGraph danglers = pg.pruneDanglingPages(1);

            assertEquals(2, pg.size());
            assertTrue("page graph contains wrong links", pg.getOutboundLinks("www.0.com")
                .contains("www.1.com"));

            assertEquals(2, danglers.size());
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.1.com")
                .contains("www.2.com"));
        } finally {
            pg.close();
        }
    }

    @Test
    public void collectAndRemoveDanglingPages_shouldRemoveLevelOneDanglerInThreeIterations() {
        PageGraph pg = PageGraphFactory.memoryDBBackedPageGraph();
        try {
            pg.add("www.0.com", "www.1.com");
            pg.add("www.1.com", "www.2.com");
            pg.add("www.2.com", "www.3.com");
            pg.add("www.3.com", "www.4.com");

            pg.add("www.0.com", "www.5.com");
            pg.add("www.1.com", "www.6.com");
            pg.add("www.2.com", "www.7.com");

            PageGraph danglers = pg.pruneDanglingPages(3);

            assertEquals(2, pg.size());
            assertTrue("page graph contains wrong links", pg.getOutboundLinks(0)
                .contains(1));

            assertEquals(8, danglers.size());
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.0.com")
                .contains("www.5.com"));
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.1.com")
                .contains("www.2.com"));
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.1.com")
                .contains("www.6.com"));
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.2.com")
                .contains("www.3.com"));
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.2.com")
                .contains("www.7.com"));
            assertTrue("page graph contains wrong links", danglers.getOutboundLinks("www.3.com")
                .contains("www.4.com"));
        } finally {
            pg.close();
        }
    }

    @Test
    public void merge_shouldContainOldAndNewValues() {
        PageGraph primary = PageGraphFactory.memoryDBBackedPageGraph();
        PageGraph secondary = null;
        try {
            primary.add("www.0.com", "www.1.com");
            primary.add("www.1.com", "www.2.com");

            secondary = PageGraphFactory.memoryDBBackedPageGraph();
            secondary.add("www.3.com", "www.4.com");
            secondary.add("www.5.com", "www.6.com");

            primary.merge(secondary);
            assertEquals("merged results have wrong size", 7, primary.size());
            assertTrue("merged results are missing values", primary.getOutboundLinks(0)
                .contains(1));
            assertTrue("merged results are missing values", primary.getOutboundLinks(1)
                .contains(2));
            assertTrue("merged results are missing values", primary.getOutboundLinks(3)
                .contains(4));
            assertTrue("merged results are missing values", primary.getOutboundLinks(5)
                .contains(6));
        } finally {
            primary.close();
            if (secondary != null) {
                secondary.close();
            }
        }
    }
}
