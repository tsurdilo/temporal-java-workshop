package io.workshop.c4s8.oneattimeworkflow;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class MyChildWorkflowImpl implements MyChildWorkflow {
    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public void execute(String input) {
        logger.info("In MyChildWorkflow: " + Workflow.getInfo().getWorkflowId());
        // sleep for demo
        Workflow.sleep(Duration.ofSeconds(1));
    }
}
