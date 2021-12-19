package io.workshop.c2s1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.workshop.c2s1.model.Account;
import io.workshop.c2s1.model.Customer;

import java.time.Duration;

public class VersioningStarter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    public static void main(String[] args) {
        Customer customer = new Customer("c1", "John", "john@john.com", "new",
                Duration.ofMinutes(2));
        WorkflowOptions newCustomerWorkflowOptions =
                WorkflowOptions.newBuilder()
                        .setWorkflowId(customer.getAccountNum())
                        .setTaskQueue(VersioningWorker.TASK_QUEUE)
                        .build();
        CustomerWorkflow newCustomerWorkflow = client.newWorkflowStub(CustomerWorkflow.class, newCustomerWorkflowOptions);

        Account account = newCustomerWorkflow.execute(customer);

        System.out.println("Account amount: " + account.getAmount());
    }
}
