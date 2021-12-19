package io.workshop.c1s3;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import static io.workshop.c1s3.S3WFUtils.client;
import static io.workshop.c1s3.S3WFUtils.taskQueue;

public class ParentWorker {
    public static void main(String[] args) {

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        // Can be called multiple times
        // Register both parent and child workflows
        worker.registerWorkflowImplementationTypes(ParentWorkflowImpl.class,
                ChildWorkflowImpl.class);

        workerFactory.start();
    }
}
