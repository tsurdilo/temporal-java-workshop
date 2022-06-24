package io.workshop.c5s3.interceptor;

import io.temporal.common.interceptors.ActivityInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkerInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;

public class OrderInterceptor implements WorkerInterceptor {

    @Override
    public WorkflowInboundCallsInterceptor interceptWorkflow(WorkflowInboundCallsInterceptor next) {
        return new OrderWorkflowInboundCallsInterceptor(next);
    }

    @Override
    public ActivityInboundCallsInterceptor interceptActivity(ActivityInboundCallsInterceptor next) {
        return new OrderActivityInboundCallsInterceptor(next);
    }
}

