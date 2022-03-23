package io.workshop.c4s1;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ChildWorkflowOneImpl implements ChildWorkflowOne {
    @Override
    public String execChildOne(String input) {
        Workflow.sleep(Duration.ofSeconds(3));
        return "cOne: " + input;
    }
}
