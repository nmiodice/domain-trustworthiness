package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;

public abstract class HandlerTestBase {
    protected PersistenceAdaptor persistenceMock = mock(PersistenceAdaptor.class);
    protected WorkResponse validWorkResponse = WorkResponse.builder()
        .source("http://www.google.com")
        .destinations(Arrays.asList("http://www.cnn.com", "http://www.twitter.com"))
        .build();
    WorkResponse emptyDestinationsResponse = WorkResponse.builder()
        .source("http://www.google.com")
        .destinations(Collections.emptyList())
        .build();
    private WorkResponse emptySourceResponse = WorkResponse.builder()
        .source("")
        .destinations(Arrays.asList("http://www.cnn.com", "http://www.twitter.com"))
        .build();
    private WorkResponse nullSourceResponse = WorkResponse.builder()
        .source(null)
        .destinations(Arrays.asList("http://www.cnn.com", "http://www.twitter.com"))
        .build();
    private WorkResponse nullDestinationsResponse = WorkResponse.builder()
        .source("http://www.google.com")
        .destinations(null)
        .build();

    protected abstract ResponseHandler getHandlerInstance();

    @Test(expected = NullPointerException.class)
    public void handle_shouldFailWithNullParameter() {
        getHandlerInstance().handle(null);
    }

    @Test(expected = NullPointerException.class)
    public void handle_shouldFailWithNullSource() {
        getHandlerInstance().handle(nullSourceResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handle_shouldFailWithEmptySource() {
        getHandlerInstance().handle(emptySourceResponse);
    }

    @Test(expected = NullPointerException.class)
    public void handle_shouldFailWithNullDestinations() {
        getHandlerInstance().handle(nullDestinationsResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handle_shouldFailWithEmptyDestinations() {
        getHandlerInstance().handle(emptyDestinationsResponse);
    }

    @Test
    public void handle_shouldSucceedForValidInput() {
        getHandlerInstance().handle(validWorkResponse);
    }
}
