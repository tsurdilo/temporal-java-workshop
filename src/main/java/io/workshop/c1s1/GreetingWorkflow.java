package io.workshop.c1s1;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GreetingWorkflow {
    @WorkflowMethod
    String greet(Customer customer);

    @SignalMethod
    void setCustomer(Customer customer);

    @QueryMethod
    Customer getCustomer();
}
