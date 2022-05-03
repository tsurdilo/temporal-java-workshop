package io.workshop.c4s3;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import io.workshop.c4s2.C4S2WorkflowImpl;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // TODO - show how to disabl client side signal calls
//    public static final WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
//            .setInterceptors(new MyWorkflowClientInterceptor())
//            .build();
//    public static final WorkflowClient client = WorkflowClient.newInstance(service, options);
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    public static final String taskQueue = "c4s3TaskQueue";

    private static final String workflowId1 = "c4s3WorkflowOne";
    private static final String workflowId2 = "c4s3WorkflowTwo";

    public static void main(String[] args) {
        createWorker();

        WorkflowTwo workflowTwo = client.newWorkflowStub(WorkflowTwo.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId2)
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflowTwo::exec);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WorkflowOne workflowOne = client.newWorkflowStub(WorkflowOne.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId1)
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflowOne::exec);

        for(int i = 0; i < 10; i++) {
            workflowTwo.setData("from client!");
        }

    }

    private static void createWorker() {
        // TODO - show how to disable external workflow stub signals
//        WorkerFactoryOptions wfo =
//                WorkerFactoryOptions.newBuilder()
//                        .setWorkerInterceptors(new MyWorkerInterceptor())
//                        .validateAndBuildWithDefaults();
//        WorkerFactory workerFactory = WorkerFactory.newInstance(client, wfo);

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        // Show with factory options
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(WorkflowOneImpl.class, WorkflowTwoImpl.class);

        workerFactory.start();
    }
}
