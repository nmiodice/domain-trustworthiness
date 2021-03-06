package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        allSeen.put(validWorkResponse.getSource(), true);

        noneSeen = validWorkResponse.getDestinations()
            .stream()
            .collect(Collectors.toMap(Function.identity(), x -> false));
        noneSeen.put(validWorkResponse.getSource(), false);

        doReturn(allSeen).when(persistenceMock)
            .isInEdgeGraph(any());
    }

    @Override
    protected PayloadHandler getHandlerInstance() {
        return handler;
    }

    @Test
    public void handle_shouldReturnNullIfAllDestinationsAreSeen() {
        doReturn(allSeen).when(persistenceMock)
            .isInEdgeGraph(any());
        assertNull(handler.handle(validWorkResponse));
    }

    @Test
    public void handle_shouldNotFilterURLsIfNeverSeen() {
        doReturn(noneSeen).when(persistenceMock)
            .isInEdgeGraph(any());
        WorkResponse afterHandle = handler.handle(validWorkResponse);

        assertTrue(afterHandle.getDestinations()
            .size() == 2);
    }
}
