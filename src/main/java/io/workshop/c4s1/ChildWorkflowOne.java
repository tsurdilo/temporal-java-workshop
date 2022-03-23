package io.workshop.c4s1;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ChildWorkflowOne {
    @WorkflowMethod
    String execChildOne(String input);
}
