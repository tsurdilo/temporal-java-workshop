package io.workshop.c1;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;

import static io.workshop.c1.WFUtils.client;

public class GreetingWorker {
    public static void main(String[] args) {

        WorkflowImplementationOptions workflowImplementationOptions =
                WorkflowImplementationOptions.newBuilder()
                        //.setFailWorkflowExceptionTypes(NullPointerException.class)
                        .build();

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker("c1TaskQueue");

        // Can be called multiple times
        worker.registerWorkflowImplementationTypes(workflowImplementationOptions, GreetingWorkflowImpl.class);

        workerFactory.start();
    }
}
