package io.workshop.c5s1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c4s9.MyWorkflowImpl;

public class Starter {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c5s1TaskQueue";


    public static void main(String[] args) {
        createWorker();

        // start the 3 requester workflows
        Workflows.WorkflowA workflowA = client.newWorkflowStub(Workflows.WorkflowA.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("workflowA")
                        .setTaskQueue(taskQueue)
                        .build());
        Workflows.WorkflowB workflowB = client.newWorkflowStub(Workflows.WorkflowB.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("workflowB")
                        .setTaskQueue(taskQueue)
                        .build());
        Workflows.WorkflowC workflowC = client.newWorkflowStub(Workflows.WorkflowC.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("workflowC")
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflowA::exec);
        WorkflowClient.start(workflowB::exec);
        WorkflowClient.start(workflowC::exec);

        // ....
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(WorkflowsImpls.WorkflowAImpl.class,
                WorkflowsImpls.WorkflowBImpl.class,
                WorkflowsImpls.WorkflowCImpl.class,
                WorkflowsImpls.BulkRequesterWorkflowImpl.class);
        worker.registerActivitiesImplementations(new ActivitiesImpl());
        workerFactory.start();
    }
}
