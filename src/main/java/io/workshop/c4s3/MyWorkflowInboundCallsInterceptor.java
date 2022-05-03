package io.workshop.c4s3;

import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptorBase;
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInfo;

public class MyWorkflowInboundCallsInterceptor extends WorkflowInboundCallsInterceptorBase {
    private WorkflowInfo workflowInfo;

    public MyWorkflowInboundCallsInterceptor(WorkflowInboundCallsInterceptor next) {
        super(next);
    }

    @Override
    public void init(WorkflowOutboundCallsInterceptor outboundCalls) {
        this.workflowInfo = Workflow.getInfo();
        super.init(new MyWorkflowOutboundCallsInterceptor(outboundCalls));
    }
}
