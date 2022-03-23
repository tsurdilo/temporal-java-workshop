package io.workshop.c4s2;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import io.workshop.c4s1.ChildWorkflowOneImpl;
import io.workshop.c4s1.ChildWorkflowTwoImpl;
import io.workshop.c4s1.MainWorkflow;
import io.workshop.c4s1.MainWorkflowImpl;

import java.util.Collections;

public class Starter {
    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c4s2TaskQueue";

    private static final String workflowId = "c4s2ParentWorkflow";

    public static void main(String[] args) {
        createWorker();

        C4S2Workflow workflow = client.newWorkflowStub(C4S2Workflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .setMemo(Collections.singletonMap("mymemo", "mymemovalue"))
                .build());

        workflow.exec();

        System.exit(1);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(C4S2WorkflowImpl.class);
        worker.registerActivitiesImplementations(new C4S2ActivitiesImpl());

        workerFactory.start();
    }
}
