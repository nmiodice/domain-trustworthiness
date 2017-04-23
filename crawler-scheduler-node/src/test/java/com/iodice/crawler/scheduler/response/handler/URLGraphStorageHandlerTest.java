package com.iodice.crawler.scheduler.response.handler;

import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class URLGraphStorageHandlerTest extends HandlerTestBase {
    private URLGraphStorageHandler handler;

    public URLGraphStorageHandlerTest() {
        handler = new URLGraphStorageHandler(persistenceMock);
    }

    @Test
    public void handle_shouldStoreAllEdges() {
        handler.handle(validWorkResponse);

        verify(persistenceMock, times(1)).storeURLEdges(validWorkResponse.getSource(),
            validWorkResponse.getDestinations());
    }

    @Override
    protected ResponseHandler getHandlerInstance() {
        return handler;
    }
}
