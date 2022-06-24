package io.workshop.c5s4;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;
import java.util.Set;

@WorkflowInterface
public interface C5S4Workflow {
    @WorkflowMethod
    void exec();
}
