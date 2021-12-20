package io.workshop.c2s1.cronwf;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class CronWorkflowImpl implements CronWorkflow {
    @Override
    public void exec() {
        //silly
        Workflow.sleep(Duration.ofSeconds(30));
    }
}
