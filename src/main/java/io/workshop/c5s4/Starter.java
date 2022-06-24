package io.workshop.c5s4;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.time.Duration;

public class Starter {
    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        String taskQueue = "c5s4TaskQueue";

        C5S4Workflow workflow = client.newWorkflowStub(C5S4Workflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("myworkflow")
                        .setTaskQueue(taskQueue)
                        // set workflow execution timeout to time out "rogue" workflows
//                        .setWorkflowExecutionTimeout(Duration.ofMinutes(2))
                        .build());

        workflow.exec();
    }
}
