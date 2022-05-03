package io.workshop.c4s8.oneattimeworkflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface RatedWorkflow {
    @WorkflowMethod
    void oneAtTime(List<ToInvoke> toInvokeQueue);

    @SignalMethod
    void toInvoke(ToInvoke toInvoke);

    @SignalMethod
    void exit();
}
