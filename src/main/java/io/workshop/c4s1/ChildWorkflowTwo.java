package io.workshop.c4s1;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ChildWorkflowTwo {
    @WorkflowMethod
    String execChildTwo(String input);
}
