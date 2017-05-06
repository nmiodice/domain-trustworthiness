package com.iodice.crawler.scheduler.handlers.response.handler;

import com.iodice.crawler.scheduler.handlers.PayloadHandler;
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
    protected PayloadHandler getHandlerInstance() {
        return handler;
    }
}
