package io.workshop.intro;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.util.Optional;

public class GetFinalCount {

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "myTaskQueue";


    /**
     * Note- run this while Starter is still running (as it contains the worker)
     * @param args
     */
    public static void main(String[] args) {
        WorkflowStub stub = client.newUntypedWorkflowStub("2592c07a-bde8-4500-bf87-d0d5366a88c0", Optional.empty(), Optional.empty());
        String finalCount = stub.query("getCount", String.class);

        System.out.println("Final Count: " + finalCount);
    }
}
