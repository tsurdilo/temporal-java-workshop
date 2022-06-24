package io.workshop.c5s6;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ModelWorkflow {
    @WorkflowMethod
    void exec(MyModel model);
}
