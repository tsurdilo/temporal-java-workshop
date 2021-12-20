package io.workshop.c2s2;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.time.Duration;

@WorkflowInterface
public interface SleepWorkflow {
    @WorkflowMethod
    void exec(long myTime);
}
