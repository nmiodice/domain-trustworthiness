package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.mockito.Mockito.mock;

public class ResponseHandlerPipelineFactoryTest {
    private static final List<Class<? extends ResponseHandler>> EXPECTED_PIPELINE_HANDLER_CLASSES;
    private ResponseHandlerPipeline pipeline = ResponseHandlerPipelineFactory.defaultPipeline(
        mock(PersistenceAdaptor.class));

    static {
        EXPECTED_PIPELINE_HANDLER_CLASSES = new ArrayList<>();
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(FilterSeenURLsHandler.class);
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(URLGraphStorageHandler.class);
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(DomainGraphStorageHandler.class);
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(WorkQueueStorageHandler.class);
    }

    @Test
    public void defaultPipeline_shouldContainProperHandlers() {
        assertFalse(pipeline.getHandlers()
            .stream()
            .anyMatch(handler -> !EXPECTED_PIPELINE_HANDLER_CLASSES.contains(handler.getClass())));
        assertEquals(EXPECTED_PIPELINE_HANDLER_CLASSES.size(), pipeline.getHandlers().size());
    }

    /**
     * applying the handlers out of order could lead to more work being done than is really needed. this means a less
     * efficient crawl. The order is not 100% required, but is good to do for effeciency.
     */
    @Test
    public void defaultPipeline_shouldOrderHandlersProperly() {
        for (Class handlerType : EXPECTED_PIPELINE_HANDLER_CLASSES) {
            int expectedOrder = EXPECTED_PIPELINE_HANDLER_CLASSES.indexOf(handlerType);
            int actualOrder = -1;

            for (int i = 0; i < pipeline.getHandlers().size(); i++) {
                if (pipeline.getHandlers().get(i).getClass().equals(handlerType)) {
                    actualOrder = i;
                }
            }

            assertEquals("pipeline handlers are out of order", expectedOrder, actualOrder);
        }
    }
}
