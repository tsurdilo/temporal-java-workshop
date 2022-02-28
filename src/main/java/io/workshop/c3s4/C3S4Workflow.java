package io.workshop.c3s4;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface C3S4Workflow {
    @WorkflowMethod
    String exec();
}
