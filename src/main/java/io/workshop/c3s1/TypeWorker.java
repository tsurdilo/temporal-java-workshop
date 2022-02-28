package io.workshop.c3s1;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c1s1.GreetingWorkflowImpl;


public class TypeWorker {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c3TypeTaskQueue";


    public static void main(String[] args) {

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        // 1. Show multiple workflow types cannot be registered...
        // TypeWorkflow workflow type is already registered with the worker
        // TODO show how to set workflow type via annotation
//        worker.registerWorkflowImplementationTypes(TypeWorkflowImpl.class,
//                TypeWorkflowImpl.class);

        // 2. Show multiple signal names / query names
        // Duplicated name of SIGNAL: "inputSignal" declared at "public abstract void io.workshop.c3s1.TypeWorkflow
        // TODO set specific signal name
        // Duplicated name of QUERY: "getInput" declared at "public abstract java.lang.String io.workshop.c3s1
        // TODO set specific query name
//        worker.registerWorkflowImplementationTypes(TypeWorkflowImpl.class);

        // 3. Show multiple query names

        // 3. Show multiple activity types
        // "DoSomething" activity type is already registered with the worker
        // Not that "D" is upper case!!
        // TODO show how to fix this via ActivityMethod annotations
        // TODO show activity interface namePrefix
        worker.registerActivitiesImplementations(new TypeOneActivitiesImpl(),
                new TypeTwoActivitiesImpl());

        // 4. Show child workflow type

        workerFactory.start();
    }
}
