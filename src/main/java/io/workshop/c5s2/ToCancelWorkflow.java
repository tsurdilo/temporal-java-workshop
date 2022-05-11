package io.workshop.c5s2;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ToCancelWorkflow {
    @WorkflowMethod
    String exec(String input);

    @SignalMethod
    void doCancel();
}
