package io.workshop.c4s8.ratelimitactivities;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyWorkflow {
    @WorkflowMethod
    String exec(String input);
}
