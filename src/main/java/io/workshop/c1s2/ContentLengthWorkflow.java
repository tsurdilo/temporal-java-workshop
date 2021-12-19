package io.workshop.c1s2;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ContentLengthWorkflow {
    @WorkflowMethod
    ContentLengthInfo execute();
}
