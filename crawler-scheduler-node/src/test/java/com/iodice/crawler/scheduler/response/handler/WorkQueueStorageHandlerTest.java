package com.iodice.crawler.scheduler.response.handler;

import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class WorkQueueStorageHandlerTest extends HandlerTestBase {
    private WorkQueueStorageHandler handler;

    public WorkQueueStorageHandlerTest() {
        handler = new WorkQueueStorageHandler(persistenceMock);
    }

    @Test
    public void handle_shouldEnqueueAllDestinations() {
        handler.handle(validWorkResponse);

        verify(persistenceMock, times(1)).enqueueURLS(validWorkResponse.getDestinations());
    }

    @Override
    protected ResponseHandler getHandlerInstance() {
        return handler;
    }
}
