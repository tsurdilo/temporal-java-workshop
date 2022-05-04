package io.workshop.c4s9;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.time.Duration;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c4s9TaskQueue";
    public static final String workflowId = "BusyLoop";

    public static void main(String[] args) {
        createWorker();

        io.workshop.c4s9.MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .setWorkflowRunTimeout(Duration.ofMinutes(1))
                        .build());

        WorkflowClient.start(workflow::run, new MyObject());
        sleep(10);
        // signal to complete
        workflow.complete();

        String result = WorkflowStub.fromTyped(workflow).getResult(String.class);
        System.out.println("Result: " + result);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);
        workerFactory.start();
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
