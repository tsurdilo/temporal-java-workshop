package io.workshop.c4s3;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface WorkflowOne {
    @WorkflowMethod
    void exec();
}
