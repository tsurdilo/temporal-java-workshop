package io.workshop.c4s3;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class WorkflowTwoImpl implements WorkflowTwo {
    private String data;

    @Override
    public void exec() {
        Workflow.sleep(Duration.ofSeconds(50));
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }
}
