package io.workshop.c3s1;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TypeChildWorkflow {
    @WorkflowMethod
    String executeChild(String input);

    @SignalMethod
    void inputSignal();

    @QueryMethod
    String getInput();
}
