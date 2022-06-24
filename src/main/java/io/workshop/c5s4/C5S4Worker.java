package io.workshop.c5s4;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.NonDeterministicException;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;

public class C5S4Worker {
    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        String taskQueue = "c5s4TaskQueue";

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        WorkflowImplementationOptions options =
                WorkflowImplementationOptions.newBuilder()
                        .setFailWorkflowExceptionTypes(NonDeterministicException.class)
                        .build();
        worker.registerWorkflowImplementationTypes(options, C5S4WorkflowImpl.class);
        worker.registerActivitiesImplementations(new C5S4ActivityImpl());
        workerFactory.start();
    }
}
