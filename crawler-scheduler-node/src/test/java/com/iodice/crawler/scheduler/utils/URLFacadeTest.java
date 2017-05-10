package com.iodice.crawler.scheduler.utils;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class URLFacadeTest {
    @Test
    public void toDomain_shouldProperlyParseWellFormattedURL() {
        assertEquals("www.cnn.com", URLFacade.toDomain("http://www.cnn.com/"));
        assertEquals("www.cnn.com", URLFacade.toDomain("https://www.cnn.com/"));
        assertEquals("www.cnn.com", URLFacade.toDomain("https://www.cnn.com////"));
        assertEquals("www.cnn.com", URLFacade.toDomain("https://www.cnn.com?a=1&b=2"));
        assertEquals("www.cnn.com", URLFacade.toDomain("https://www.cnn.com/page1"));
        assertEquals("www.cnn.com", URLFacade.toDomain("https://www.cnn.com/page1/page2"));
        assertEquals("www.cnn.com", URLFacade.toDomain("https://www.cnn.com/page1/page2?a=1&b=2"));
    }

    @Test
    public void toDomain_shouldReturnNullForPoorlyFormattedURL() {
        assertNull(URLFacade.toDomain("abc"));
        assertNull(URLFacade.toDomain("cnn.com"));
        assertNull(URLFacade.toDomain("www.cnn.com/"));
        assertNull(URLFacade.toDomain("http://www..com/"));
    }

    @Test(expected = NullPointerException.class)
    public void toDomain_shouldFailForNullInput() {
        URLFacade.toDomain(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toDomain_shouldFailForEmptyInput() {
        URLFacade.toDomain("");
    }
}
