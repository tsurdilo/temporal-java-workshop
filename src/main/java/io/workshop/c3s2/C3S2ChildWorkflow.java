package io.workshop.c3s2;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface C3S2ChildWorkflow {
    @WorkflowMethod
    public String execChild(String input);
}
