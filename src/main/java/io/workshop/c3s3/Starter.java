package io.workshop.c3s3;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c3s3TaskQueue";

    private static final String workflowId = "c3s3Workflow";


    public static void main(String[] args) {
        createWorker();

        // use typed stub to show dynamic

        C3S3Workflow workflow = client.newWorkflowStub(C3S3Workflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .build());

        // start async
        WorkflowClient.start(workflow::exec);
        // send the input1 signal via typed stub
        workflow.setInput("some input");

        // note we cannot send the input2 signal via typed stub as its not part of the wf interface (its dynamic)
        // but we can use untyped for it
        // convert typed stub to untyped
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
        untyped.signal("setInput2", "some other input");

        // possibly wait till workflow completes
        untyped.getResult(Void.class);

        // query input1 via typed stub
        System.out.println("Input1: " + workflow.getInput());
        // query input2 via untyped - dynamic handler (cannot query via typed stub its not defined in interface)
        System.out.println("Input2: " + untyped.query("getInput2", String.class));


        System.exit(0);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(C3S3WorkflowImpl.class);

        workerFactory.start();
    }
}
