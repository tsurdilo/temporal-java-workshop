package io.workshop.s3;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ParentWorkflow {
    @WorkflowMethod
    void executeParent();
}
