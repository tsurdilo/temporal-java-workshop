package io.workshop.c1s1;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;

import static io.workshop.c1s1.S1WFUtils.client;
import static io.workshop.c1s1.S1WFUtils.taskQueue;

public class GreetingWorker {
    public static void main(String[] args) {
        WorkflowImplementationOptions workflowImplementationOptions =
                WorkflowImplementationOptions.newBuilder()
                        .setFailWorkflowExceptionTypes(NullPointerException.class)
                        .build();

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        // Can be called multiple times
        worker.registerWorkflowImplementationTypes(workflowImplementationOptions, GreetingWorkflowImpl.class);

        workerFactory.start();
    }
}
