package com.iodice.crawler.scheduler.response;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import com.iodice.crawler.scheduler.response.handlers.DomainGraphStorageHandler;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.times;

public class DomainGraphStorageHandlerTest {
    private DomainGraphStorageHandler handler;
    private PersistenceAdaptor adaptorMock;

    public DomainGraphStorageHandlerTest() {
        adaptorMock = Mockito.mock(PersistenceAdaptor.class);
        handler = new DomainGraphStorageHandler(adaptorMock);
    }

    @Test
    public void handle_shouldPersistFullyValidInput() {
        handler.handle(WorkResponse.builder()
            .source("http://www.audible.com")
            .destinations(Arrays.asList("http://www.cnn.com", "http://www.twitter.com"))
            .build());

        Mockito.verify(adaptorMock, times(1))
            .storeDomainEdges("www.audible.com", new HashSet<>(Arrays.asList("www.cnn.com", "www.twitter.com")));
    }

    @Test
    public void handle_shouldPersistNonNullInput() {
        handler.handle(WorkResponse.builder()
            .source("http://www.audible.com")
            .destinations(Arrays.asList(null, "http://www.twitter.com"))
            .build());

        Mockito.verify(adaptorMock, times(1))
            .storeDomainEdges("www.audible.com", new HashSet<>(Collections.singleton("www.twitter.com")));
    }

    @Test
    public void handle_shouldPersistNonEmptyInput() {
        handler.handle(WorkResponse.builder()
            .source("http://www.audible.com")
            .destinations(Arrays.asList("", "http://www.twitter.com"))
            .build());

        Mockito.verify(adaptorMock, times(1))
            .storeDomainEdges("www.audible.com", new HashSet<>(Collections.singleton("www.twitter.com")));
    }

    @Test(expected = NullPointerException.class)
    public void handle_shouldFailWithNullParameter() {
        handler.handle(null);
    }

    @Test(expected = NullPointerException.class)
    public void handle_shouldFailWithNullSource() {
        handler.handle(WorkResponse.builder()
            .source(null)
            .destinations(Collections.singleton(""))
            .build());
    }

    @Test(expected = NullPointerException.class)
    public void handle_shouldFailWithNullDestinations() {
        handler.handle(WorkResponse.builder()
            .source("blah")
            .destinations(null)
            .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void handle_shouldFailWithEmptyDestinations() {
        handler.handle(WorkResponse.builder()
            .source("blah")
            .destinations(Collections.emptyList())
            .build());
    }

    @Test
    public void handle_shouldSucceedForValidInput() {
        handler.handle(WorkResponse.builder()
            .source("blah")
            .destinations(Collections.singletonList("blah"))
            .build());
    }
}
