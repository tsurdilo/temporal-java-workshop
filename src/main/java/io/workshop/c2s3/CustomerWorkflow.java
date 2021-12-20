package io.workshop.c2s3;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.workshop.c2s3.model.Account;
import io.workshop.c2s3.model.Customer;

@WorkflowInterface
public interface CustomerWorkflow {
    @WorkflowMethod
    Account execute(Customer customer);
}