package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

public class FilterSeenURLsHandlerTest extends HandlerTestBase {
    private FilterSeenURLsHandler handler = new FilterSeenURLsHandler(persistenceMock);
    private Map<String, Boolean> allSeen;
    private Map<String, Boolean> noneSeen;

    @Before
    public void init() {
        allSeen = validWorkResponse.getDestinations()
            .stream()
            .collect(Collectors.toMap(Function.identity(), x -> true));
        noneSeen = validWorkResponse.getDestinations()
            .stream()
            .collect(Collectors.toMap(Function.identity(), x -> false));

        doReturn(allSeen).when(persistenceMock)
            .seenURLS(any());
    }

    @Override
    protected ResponseHandler getHandlerInstance() {
        return handler;
    }

    @Test
    public void handle_shouldReturnNullIfAllDestinationsAreSeen() {
        doReturn(allSeen).when(persistenceMock)
            .seenURLS(any());
        assertNull(handler.handle(validWorkResponse));
    }

    @Test
    public void handle_shouldNotFilterURLsIfNeverSeen() {
        doReturn(noneSeen).when(persistenceMock)
            .seenURLS(any());
        WorkResponse afterHandle = handler.handle(validWorkResponse);

        assertTrue(afterHandle.getDestinations()
            .size() == 2);
    }
}
