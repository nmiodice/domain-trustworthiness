package com.iodice.crawler.scheduler.handlers.request.handler;

import com.iodice.crawler.scheduler.entity.WorkRequest;
import com.iodice.crawler.scheduler.queue.RequestQueueAdaptor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class RequestEmitterHandler extends ValidatedRequestHandler {
    private RequestQueueAdaptor queue;

    @Override
    WorkRequest validatedHandle(WorkRequest request) {
        queue.emitRequest(request);
        return request;
    }
}
