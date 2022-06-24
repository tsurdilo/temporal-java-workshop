package io.workshop.c5s5;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c5s5TaskQueue";


    public static void main(String[] args) {
        createWorker();

        WorkflowsAndActivities.LoggerWorkflow workflow = client.newWorkflowStub(WorkflowsAndActivities.LoggerWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("loggerWf")
                        .setTaskQueue(taskQueue)
                        .build());

        workflow.exec();
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(WorkflowsAndActivities.LoggerWorkflowImpl.class,
                WorkflowsAndActivities.LoggerChildWorkflowImpl.class);
        worker.registerActivitiesImplementations(new WorkflowsAndActivities.LoggerActivitiesImpl());
        workerFactory.start();
    }
}
