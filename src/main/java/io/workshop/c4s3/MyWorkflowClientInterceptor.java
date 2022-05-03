package io.workshop.c4s3;

import io.temporal.common.interceptors.WorkflowClientCallsInterceptor;
import io.temporal.common.interceptors.WorkflowClientInterceptorBase;

public class MyWorkflowClientInterceptor extends WorkflowClientInterceptorBase {
    @Override
    public WorkflowClientCallsInterceptor workflowClientCallsInterceptor(
            WorkflowClientCallsInterceptor next) {
        return new MyWorkflowClientCallsInterceptor(next);
    }
}
