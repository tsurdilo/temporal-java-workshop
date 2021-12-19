package io.workshop.c1s1;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

/**
 * Simple Greeting Workflow Impl.
 * <p>
 * Note to self:
 * Workflows can be multi-threaded - "cooperative multithreading"
 * Threads are executed on-by-one and are preempted when on a call to Temporal SDK function,
 * for example Workflow.sleep, Future.get, Workflow.await, ....
 */
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

    /**
     * Note: Signals are invoked in own thread
     * If a signal method blocks execution, it does NOT stop other signals to be delivered
     * <p>
     * Also :) You can add workflow methods inside signal methods (like call activities)...
     * but its not recommended
     *
     * @param customer
     */
    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }
}
