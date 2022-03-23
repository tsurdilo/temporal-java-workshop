package io.workshop.c4s3;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class WorkflowOneImpl implements WorkflowOne {
    @Override
    public void exec() {
       WorkflowTwo external =  Workflow.newExternalWorkflowStub(WorkflowTwo.class, "c4s3WorkflowTwo");

       for(int i = 0; i < 20; i++) {
           external.setData("someData");
           Workflow.sleep(Duration.ofSeconds(1));
       }
    }
}
