package io.workshop.c1s3;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ChildWorkflow {
    @WorkflowMethod
    String executeChild(int count);

}
