package io.workshop.c2s1.cronwf;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CronWorkflow {
    @WorkflowMethod
    public void exec();
}
