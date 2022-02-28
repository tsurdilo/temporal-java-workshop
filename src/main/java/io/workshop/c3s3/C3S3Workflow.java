package io.workshop.c3s3;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface C3S3Workflow {
    @WorkflowMethod
    void exec();

    @SignalMethod
    void setInput(String input);

    @QueryMethod
    String getInput();
}
