package io.workshop.c1s2;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import static io.workshop.c1s2.S2WFUtils.client;
import static io.workshop.c1s2.S2WFUtils.taskQueue;

public class ContentWorker {
    public static void main(String[] args) {

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        // Can be called multiple times
        worker.registerWorkflowImplementationTypes(ContentLengthWorkflowImpl.class);

        // Single activity instance is registered - activities must be thread safe!
        worker.registerActivitiesImplementations(new ContentLengthActivityImpl());

        workerFactory.start();
    }
}
