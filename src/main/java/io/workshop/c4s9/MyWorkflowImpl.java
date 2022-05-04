package io.workshop.c4s9;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class MyWorkflowImpl implements MyWorkflow {
    private MyObject myObject;

    @Override
    public String run(MyObject myObject) {
        this.myObject = myObject;

        // Busy loop that grows history - anti-pattern, dont do
//        while (!this.myObject.isCompleted()) {
//            Workflow.sleep(Duration.ofMillis(500));
//            //... call activities etc etc (look at polling samples as well)
//        }

        // use Workflow.await instead
        Workflow.await( () -> this.myObject.isCompleted());
        // call activities...
        return "done";
    }

    @Override
    public void complete() {
        this.myObject.setCompleted(true);
    }
}
