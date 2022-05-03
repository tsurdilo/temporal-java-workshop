package io.workshop.c4s8.oneattimeworkflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyChildWorkflow {
    @WorkflowMethod
    void execute(String input);
}
