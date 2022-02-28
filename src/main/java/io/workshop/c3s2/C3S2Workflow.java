package io.workshop.c3s2;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface C3S2Workflow {
    @WorkflowMethod
    String execute(String input);

    @SignalMethod
    void addInput(String input);

    @QueryMethod
    String getInput();
}
