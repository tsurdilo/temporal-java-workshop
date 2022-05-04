package io.workshop.c4s10.workflow;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class FillWorkflowImpl implements FillWorkflow {
    @Override
    public String exec(String input) {
        // do the "busy loop" (anti-pattern for a while)
        for(int i = 0; i < 50; i++) {
            Workflow.sleep(Duration.ofMillis(500));
        }
        return "done";
    }
}
