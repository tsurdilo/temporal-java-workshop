package io.workshop.c4s3;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c4s2.C4S2ActivitiesImpl;
import io.workshop.c4s2.C4S2WorkflowImpl;

public class Starter {
    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c4s3TaskQueue";

    private static final String workflowId1 = "c4s3WorkflowOne";
    private static final String workflowId2 = "c4s3WorkflowTwo";

    public static void main(String[] args) {
        createWorker();

        WorkflowTwo workflowTwo = client.newWorkflowStub(WorkflowTwo.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId2)
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflowTwo::exec);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WorkflowOne workflowOne = client.newWorkflowStub(WorkflowOne.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId1)
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflowOne::exec);

    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(WorkflowOneImpl.class, WorkflowTwoImpl.class);

        workerFactory.start();
    }
}
