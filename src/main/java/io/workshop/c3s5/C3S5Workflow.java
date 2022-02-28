package io.workshop.c3s5;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface C3S5Workflow {
    @WorkflowMethod
    int exec(int StartCount);

    @SignalMethod
    void addSignal();

    @SignalMethod
    void exit();

    @QueryMethod
    int getCount();
}
