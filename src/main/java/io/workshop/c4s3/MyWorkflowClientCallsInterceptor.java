package io.workshop.c4s3;

import io.temporal.common.interceptors.WorkflowClientCallsInterceptor;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptorBase;

public class MyWorkflowClientCallsInterceptor extends WorkflowClientCallsInterceptorBase {

    public MyWorkflowClientCallsInterceptor(WorkflowClientCallsInterceptor next) {
        super(next);
    }

    @Override
    public WorkflowSignalOutput signal(WorkflowSignalInput input) {
        // this is also bad..dont do it at home...
        return new WorkflowSignalOutput();
        // typically you would just
//        return super.signal(input);
    }
}
