package io.workshop.c5s3.interceptor;

import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptorBase;
import io.temporal.workflow.Workflow;

public class OrderWorkflowInboundCallsInterceptor extends WorkflowInboundCallsInterceptorBase {

    private boolean signalLock = true;

    public OrderWorkflowInboundCallsInterceptor(WorkflowInboundCallsInterceptor next) {
        super(next);
    }

    @Override
    public void handleSignal(SignalInput input) {
        Workflow.await(() -> signalLock);
        signalLock = false;
        try {
            super.handleSignal(input);
        } finally {
            signalLock = true;
        }
    }
}

