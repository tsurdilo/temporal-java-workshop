package io.workshop.c5s3;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SignalOrderWorkflow {
    @WorkflowMethod
    void exec();

    @SignalMethod
    void signalA(Models.AModel input);

    @SignalMethod
    void signalB(Models.BModel input);

    @SignalMethod
    void signalC(Models.CModel input);

    @SignalMethod
    void exit();
}
