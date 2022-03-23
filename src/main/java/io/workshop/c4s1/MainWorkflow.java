package io.workshop.c4s1;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MainWorkflow {
    @WorkflowMethod
    String execute(String input);
}
