package io.workshop.c3s1;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SimpleWorkflow {
    @WorkflowMethod
    void exec();
}
