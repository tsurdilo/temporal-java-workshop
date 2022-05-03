package io.workshop.c4s4;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class CronWorkflowImpl implements CronWorkflow {
    @Override
    public String runit() {
        /// dummy
        Workflow.sleep(Duration.ofSeconds(1));
        return "done";
    }
}
