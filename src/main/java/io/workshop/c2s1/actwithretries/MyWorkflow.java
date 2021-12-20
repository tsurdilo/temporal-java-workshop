package io.workshop.c2s1.actwithretries;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyWorkflow {
    @WorkflowMethod
    void exec();
}
