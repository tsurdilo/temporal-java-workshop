package io.workshop.c2s2;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.workshop.c2s2.model.Account;
import io.workshop.c2s2.model.Customer;

@WorkflowInterface
public interface CustomerWorkflow {
    @WorkflowMethod
    Account execute(Customer customer);
}