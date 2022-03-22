package io.workshop.docssample;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GreetingsWorkflow {
    @WorkflowMethod
    String greetCustomer();

    @QueryMethod
    Customer getCustomer();

    @SignalMethod
    void addCustomer(Customer customer);
}
