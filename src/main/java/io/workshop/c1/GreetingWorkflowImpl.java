package io.workshop.c1;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class GreetingWorkflowImpl implements GreetingWorkflow {

    private Customer customer;
    private Logger logger = Workflow.getLogger(this.getClass().getName());


    @Override
    public String greet(Customer customer) {

        logger.info("My Id: " + Workflow.getInfo().getWorkflowId());
        logger.info("My runId" + Workflow.getInfo().getRunId());
        logger.info("My task queue: " + Workflow.getInfo().getTaskQueue());

//        if(customer != null) {
            this.customer = customer;
//        }

        Workflow.sleep(Duration.ofSeconds(5));

        return "Hello " + this.customer.getName();
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }
}
