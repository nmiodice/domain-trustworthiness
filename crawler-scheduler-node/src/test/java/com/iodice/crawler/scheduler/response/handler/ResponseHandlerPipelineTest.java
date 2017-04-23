package com.iodice.crawler.scheduler.response.handler;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ResponseHandlerPipelineTest extends HandlerTestBase {
    private ResponseHandler firstHandler;
    private ResponseHandler secondHandler;
    private ResponseHandlerPipeline pipeline;

    @Before
    public void init() {
        firstHandler = mock(ResponseHandler.class);
        secondHandler = mock(ResponseHandler.class);
        pipeline = ResponseHandlerPipeline.builder()
            .handler(firstHandler)
            .handler(secondHandler)
            .build();

    }

    @Test
    public void handle_shouldCallBothHandlersIfInputUnchanged() {
        doReturn(validWorkResponse).when(firstHandler)
            .handle(any());
        doReturn(validWorkResponse).when(secondHandler)
            .handle(any());

        pipeline.handle(validWorkResponse);

        verify(firstHandler, times(1)).handle(any());
        verify(secondHandler, times(1)).handle(any());
    }

    @Test
    public void handle_shouldNotContinueIfHandlerReturnsNull() {
        doReturn(null).when(firstHandler)
            .handle(any());

        pipeline.handle(validWorkResponse);

        verify(firstHandler, times(1)).handle(any());
        verify(secondHandler, times(0)).handle(any());
    }

    @Test
    public void handle_shouldNotContinueIfHandlerReturnsEmptyDestinations() {
        doReturn(emptyDestinationsResponse).when(firstHandler)
            .handle(any());

        pipeline.handle(validWorkResponse);

        verify(firstHandler, times(1)).handle(any());
        verify(secondHandler, times(0)).handle(any());
    }

    @Override
    protected ResponseHandler getHandlerInstance() {
        return pipeline;
    }
}
