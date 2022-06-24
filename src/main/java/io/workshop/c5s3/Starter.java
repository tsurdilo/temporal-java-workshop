package io.workshop.c5s3;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c5s3TaskQueue";

    public static void main(String[] args) {
        createWorker();

        SignalOrderWorkflow workflow = client.newWorkflowStub(SignalOrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("orderWF")
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(workflow::exec);


        // send different signals
        workflow.signalA(new Models.AModel("A-1"));
        workflow.signalB(new Models.BModel("B-1"));
        workflow.signalC(new Models.CModel("C-1"));
        workflow.signalA(new Models.AModel("A-2"));
        workflow.signalB(new Models.BModel("B-2"));
        workflow.signalC(new Models.CModel("C-2"));


        try {
            Thread.sleep(20 * 1000);

            // signal exit
            workflow.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createWorker() {

        // TODO show last
//        WorkerFactoryOptions wfo =
//                WorkerFactoryOptions.newBuilder()
//                        .setWorkerInterceptors(new OrderInterceptor())
//                        .build();
//        WorkerFactory workerFactory = WorkerFactory.newInstance(client, wfo);

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);

        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(SignalOrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderActivityImpl());
        workerFactory.start();
    }
}
