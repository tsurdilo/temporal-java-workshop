package io.workshop.c5s6;

import io.temporal.workflow.Workflow;

public class ModelWorkflowImpl implements ModelWorkflow {
    @Override
    public void exec(MyModel model) {
        Workflow.getLogger(this.getClass().getName()).info("** Model: " + model);
    }
}
