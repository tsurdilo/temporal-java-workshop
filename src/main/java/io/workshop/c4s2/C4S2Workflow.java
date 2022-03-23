package io.workshop.c4s2;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface C4S2Workflow {
    @WorkflowMethod
    void exec();
}
