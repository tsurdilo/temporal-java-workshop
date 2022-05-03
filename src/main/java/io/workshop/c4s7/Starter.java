package io.workshop.c4s7;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c4s5.Activities;
import io.workshop.c4s5.Workflows;

import java.time.Duration;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c4s5TaskQueue";
    public static final String workflowId = "Recovery";

    public static void main(String[] args) {
        createWorker();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .setWorkflowRunTimeout(Duration.ofMinutes(1))
                        .build());

        try {
            System.out.println("Result: " + workflow.exec());
        } catch (WorkflowFailedException e) {
            System.out.println("Workflow Failed: " + e.getMessage());
        }
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);

        worker.registerActivitiesImplementations(new RecoverableActivityImpl());
        workerFactory.start();
    }
}
