package io.workshop.c4s3;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface WorkflowTwo {
    @WorkflowMethod
    void exec();

    @SignalMethod
    void setData(String data);
}
