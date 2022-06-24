package io.workshop.c5s4;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.*;

public class C5S4Worker {
    public static void main(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        String taskQueue = "c5s4TaskQueue";

        WorkerFactoryOptions workerFactoryOptions =
                WorkerFactoryOptions.newBuilder()
                        // default is 600 - across all workers created from this factory
                        // note this is not the worker thread count
                        // set that via setMaxWorkflowThreadCount(int)
                        .setWorkflowCacheSize(600)
                        .build();
        WorkerFactory workerFactory = WorkerFactory.newInstance(client, workerFactoryOptions);

        Worker worker = workerFactory.newWorker(taskQueue);

//        WorkflowImplementationOptions options =
//                WorkflowImplementationOptions.newBuilder()
//                        .setFailWorkflowExceptionTypes(NonDeterministicException.class)
//                        .build();
//        worker.registerWorkflowImplementationTypes(options, C5S4WorkflowImpl.class);
        worker.registerWorkflowImplementationTypes(C5S4WorkflowImpl.class);
        worker.registerActivitiesImplementations(new C5S4ActivityImpl());
        workerFactory.start();
    }
}
