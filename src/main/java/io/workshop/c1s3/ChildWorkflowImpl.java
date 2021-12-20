package io.workshop.c1s3;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class ChildWorkflowImpl implements ChildWorkflow {

    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public String executeChild(int count) {

        // Workflow.getInfo is your friend :)
        logger.info("Child id: " + Workflow.getInfo().getWorkflowId());
        logger.info("Parent id: " + Workflow.getInfo().getParentWorkflowId());

        Workflow.sleep(Duration.ofMinutes(5));
        return "Child workflow done: " + count;
    }
}
