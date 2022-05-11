package io.workshop.c5s2;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ToCancelChildWorkflow {
    @WorkflowMethod
    String execChild(String input);
}
