package io.workshop.c4s5;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.time.Duration;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c4s5TaskQueue";
    public static final String workflowId = "ChildFailureWorkflow";

    public static void main(String[] args) {
        createWorker();

        Workflows.MyWorkflow workflow = client.newWorkflowStub(Workflows.MyWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .setWorkflowRunTimeout(Duration.ofMinutes(1))
                        .build());

        try {
            System.out.println("Result: " + workflow.run());
        } catch (WorkflowFailedException e) {
            // wrapped ChildWorkflowFailures, last has ActivityFailure as Cause which has applicationfailure as cause
            ApplicationFailure failure = (ApplicationFailure) e.getCause().getCause().getCause().getCause().getCause();
            System.out.println("**** CLIENT FAILURE: " + failure.getType());
        }
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(Workflows.MyWorkflowImpl.class,
                Workflows.MyChildWorkflowAImpl.class,
                Workflows.MyChildWorkflowBImpl.class,
                Workflows.MyChildWorkflowCImpl.class);

        worker.registerActivitiesImplementations(new Activities.MyActivitiesImpl());
        workerFactory.start();
    }
}
