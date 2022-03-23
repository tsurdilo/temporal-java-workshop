package io.workshop.c4s1;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ChildWorkflowTwoImpl implements ChildWorkflowTwo {
    @Override
    public String execChildTwo(String input) {
        Workflow.sleep(Duration.ofSeconds(3));
        return "cTwo: " + input;
    }
}
