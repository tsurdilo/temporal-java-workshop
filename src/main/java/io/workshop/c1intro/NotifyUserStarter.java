package io.workshop.c1intro;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.workshop.c1s2.ContentLengthWorkflow;

import static io.workshop.c1s2.S2WFUtils.client;
import static io.workshop.c1s2.S2WFUtils.taskQueue;

public class NotifyUserStarter {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "myTaskQueue";

    public static void main(String[] args) {

        NotifyUserAccounts workflow = client.newWorkflowStub(
                NotifyUserAccounts.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("notifyAccounts")
                        .setTaskQueue(taskQueue)
                        .build()
        );


        workflow.notify(new String[] { "Account1", "Account2", "Account3", "Account4", "Account5",
                "Account6", "Account7", "Account8", "Account9", "Account10"});
    }
}
