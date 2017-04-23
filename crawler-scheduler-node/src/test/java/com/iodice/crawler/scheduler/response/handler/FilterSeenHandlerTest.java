package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.entity.WorkResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

public class FilterSeenHandlerTest extends HandlerTestBase {
    private FilterSeenHandler handler = new FilterSeenHandler(persistenceMock);
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

        doReturn(allSeen).when(persistenceMock).seenURLS(any());
    }

    @Override
    protected ResponseHandler getHandlerInstance() {
        return handler;
    }

    @Test
    public void handle_shouldFilterURLsIfSeen() {
        doReturn(allSeen).when(persistenceMock).seenURLS(any());
        WorkResponse afterHandle = handler.handle(validWorkResponse);

        assertTrue(afterHandle.getDestinations().isEmpty());
    }

    @Test
    public void handle_shouldNotFilterURLsIfNeverSeen() {
        doReturn(noneSeen).when(persistenceMock).seenURLS(any());
        WorkResponse afterHandle = handler.handle(validWorkResponse);

        assertTrue(afterHandle.getDestinations().size() == 2);
    }
}
