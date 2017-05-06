package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.handlers.PayloadHandler;
import com.iodice.crawler.scheduler.utils.URLFacade;
import org.junit.Test;

import java.util.Collections;

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
        doReturn(Integer.MAX_VALUE).when(persistenceMock)
            .getDomainSeenCount(URLFacade.toDomain("http://www.cnn.com"));
        doReturn(0).when(persistenceMock)
            .getDomainSeenCount(URLFacade.toDomain("http://www.twitter.com"));

        handler.handle(validWorkResponse);

        verify(persistenceMock, times(1)).enqueueURLS(Collections.singletonList("http://www.twitter.com"));
    }

    @Override
    protected PayloadHandler getHandlerInstance() {
        return handler;
    }
}
