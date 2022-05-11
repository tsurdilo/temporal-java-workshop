package io.workshop.c5s2;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c5s2TaskQueue";

    public static void main(String[] args) {
        createWorker();

        ToCancelWorkflow workflow = client.newWorkflowStub(ToCancelWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("ToCancelParent")
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflow::exec, "Hello");

        try {
            Thread.sleep( 2 * 1000 );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // send signal
        workflow.doCancel();

        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
        String result = untyped.getResult(String.class);
        System.out.println("In client, result: " + result);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(ToCancelWorkflowImpl.class, ToCancelChildWorkflowImpl.class);
        worker.registerActivitiesImplementations(new ToCancelActivitiesImpl());
        workerFactory.start();
    }
}
