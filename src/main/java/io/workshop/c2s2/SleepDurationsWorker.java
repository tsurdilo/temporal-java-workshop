package io.workshop.c2s2;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c2s3.CustomerWorkflowImpl;

public class SleepDurationsWorker {
    public static final String TASK_QUEUE = "sleepingWorkflowTaskQueue";
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    private static final WorkflowClient client = WorkflowClient.newInstance(service);
    private static final WorkerFactory factory = WorkerFactory.newInstance(client);

    public static void main(String[] args) {
        try {
            Worker worker = factory.newWorker(TASK_QUEUE);
            worker.registerWorkflowImplementationTypes(SleepWorkflowImpl.class);

            factory.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
