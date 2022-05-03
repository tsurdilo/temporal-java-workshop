package io.workshop.c4s3;

import io.temporal.common.interceptors.ActivityInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkerInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;

public class MyWorkerInterceptor implements WorkerInterceptor {
    @Override
    public WorkflowInboundCallsInterceptor interceptWorkflow(WorkflowInboundCallsInterceptor next) {
        return new MyWorkflowInboundCallsInterceptor(next);
    }

    @Override
    public ActivityInboundCallsInterceptor interceptActivity(ActivityInboundCallsInterceptor next) {
        return null;
    }
}
