package io.workshop.c4s1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c3s5.C3S5Workflow;
import io.workshop.c3s5.C3S5WorkflowImpl;

public class Starter {
    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c4s1TaskQueue";

    private static final String workflowId = "c4s1ParentWorkflow";


    public static void main(String[] args) {
        createWorker();

        MainWorkflow workflow = client.newWorkflowStub(MainWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .build());

        String result = workflow.execute("hello");
        System.out.println("Res: " + result);
        System.exit(1);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(MainWorkflowImpl.class, ChildWorkflowOneImpl.class, ChildWorkflowTwoImpl.class);

        workerFactory.start();
    }
}