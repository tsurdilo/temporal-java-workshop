package io.workshop.c4s8.ratelimitactivities;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;

import java.time.Duration;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c4s8TaskQueue";
    public static final String workflowId = "RateLimited";

    public static void main(String[] args) {
        createWorker();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .setWorkflowRunTimeout(Duration.ofMinutes(1))
                        .build());

        try {
            System.out.println("End Result: " + workflow.exec("Some Input"));
        } catch (WorkflowFailedException e) {
            System.out.println("Workflow Failed: " + e.getMessage());
        }
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);

        WorkerOptions workerOptions = WorkerOptions.newBuilder()
                .setMaxConcurrentActivityExecutionSize(1)
                .build();

        Worker worker = workerFactory.newWorker(taskQueue);
//        Worker worker = workerFactory.newWorker(taskQueue, workerOptions);

        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);
        worker.registerActivitiesImplementations(new RateActivitiesImpl());
        workerFactory.start();
    }
}
