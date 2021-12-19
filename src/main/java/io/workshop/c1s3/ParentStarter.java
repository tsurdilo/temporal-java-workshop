package io.workshop.c1s3;

import io.temporal.client.WorkflowOptions;

import static io.workshop.c1s3.S3WFUtils.client;
import static io.workshop.c1s3.S3WFUtils.taskQueue;

public class ParentStarter {
    // Domain-specific workflow id
    private static final String workflowId = "c3ParentWorkflow";

    public static void main(String[] args) {
        ParentWorkflow workflow = client.newWorkflowStub(
                ParentWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        workflow.executeParent();

        System.exit(0);
    }
}
