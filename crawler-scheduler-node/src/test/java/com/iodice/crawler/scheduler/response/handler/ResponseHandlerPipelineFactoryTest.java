package com.iodice.crawler.scheduler.response.handler;

import com.iodice.crawler.scheduler.persistence.PersistenceAdaptor;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static org.mockito.Mockito.mock;

public class ResponseHandlerPipelineFactoryTest {
    private static final Set<Class> EXPECTED_PIPELINE_HANDLER_CLASSES;

    static {
        EXPECTED_PIPELINE_HANDLER_CLASSES = new HashSet<>();
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(DomainGraphStorageHandler.class);
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(FilterSeenHandler.class);
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(URLGraphStorageHandler.class);
        EXPECTED_PIPELINE_HANDLER_CLASSES.add(WorkQueueStorageHandler.class);
    }

    @Test
    public void defaultPipeline_shouldContainProperHandlers() {
        ResponseHandlerPipeline pipeline = ResponseHandlerPipelineFactory.defaultPipeline(
            mock(PersistenceAdaptor.class));

        assertFalse(pipeline.getHandlers()
            .stream()
            .anyMatch(handler -> !EXPECTED_PIPELINE_HANDLER_CLASSES.contains(handler.getClass())));
    }
}
