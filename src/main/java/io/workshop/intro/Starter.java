package io.workshop.intro;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "myTaskQueue";

    public static final String[] customerIds = new String[]{"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10"};

    public static void main(String[] args) {

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        // Can be called multiple times
        worker.registerWorkflowImplementationTypes(NotifyUserAccountsWorkflow.class);
//        worker.registerActivitiesImplementations(new UserActivity());

        workerFactory.start();

//        WorkflowOptions updateCustomersOptions =
//                WorkflowOptions.newBuilder()
//                        .setTaskQueue(taskQueue)
//                        .build();
//
//        NotifyUserAccounts workflow = client.newWorkflowStub(NotifyUserAccounts.class, updateCustomersOptions);
//
//        workflow.notify(customerIds);

    }
}
