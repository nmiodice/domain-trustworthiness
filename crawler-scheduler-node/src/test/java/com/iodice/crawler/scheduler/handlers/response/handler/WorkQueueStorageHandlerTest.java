package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import com.iodice.crawler.scheduler.utils.URLFacade;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class WorkQueueStorageHandlerTest extends HandlerTestBase {
    private WorkQueueStorageHandler handler;

    public WorkQueueStorageHandlerTest() {
        handler = new WorkQueueStorageHandler(persistenceMock);
    }

    @Test
    public void handle_shouldEnqueueOnlyLowCountDestinations() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put(URLFacade.toDomain("http://www.cnn.com"), Integer.MAX_VALUE);
        counts.put(URLFacade.toDomain("http://www.twitter.com"), 0);

        doReturn(counts).when(persistenceMock).getDomainScheduledCount(any());
        handler.handle(validWorkResponse);

        verify(persistenceMock, times(1)).enqueueURLs(Collections.singleton("http://www.twitter.com"));
    }

    @Override
    protected PayloadHandler getHandlerInstance() {
        return handler;
    }
}
