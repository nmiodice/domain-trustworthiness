package com.iodice.crawler.scheduler.response.handlers;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.response.HandlerTestBase;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.times;

public class DomainGraphStorageHandlerTest extends HandlerTestBase {
    private DomainGraphStorageHandler handler;

    public DomainGraphStorageHandlerTest() {
        handler = new DomainGraphStorageHandler(persistenceMock);
    }

    @Test
    public void handle_shouldPersistFullyValidInput() {
        handler.handle(validWorkResponse);

        Mockito.verify(persistenceMock, times(1))
            .storeDomainEdges("www.google.com", new HashSet<>(Arrays.asList("www.cnn.com", "www.twitter.com")));
    }

    @Test
    public void handle_shouldPersistNonNullInput() {
        handler.handle(WorkResponse.builder()
            .source("http://www.audible.com")
            .destinations(Arrays.asList(null, "http://www.twitter.com"))
            .build());

        Mockito.verify(persistenceMock, times(1))
            .storeDomainEdges("www.audible.com", new HashSet<>(Collections.singleton("www.twitter.com")));
    }

    @Test
    public void handle_shouldPersistNonEmptyInput() {
        handler.handle(WorkResponse.builder()
            .source("http://www.audible.com")
            .destinations(Arrays.asList("", "http://www.twitter.com"))
            .build());

        Mockito.verify(persistenceMock, times(1))
            .storeDomainEdges("www.audible.com", new HashSet<>(Collections.singleton("www.twitter.com")));
    }

    @Override
    protected ResponseHandler getHandlerInstance() {
        return handler;
    }
}
