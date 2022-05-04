package io.workshop.c4s2;

import io.grpc.netty.shaded.io.netty.util.Timeout;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.failure.TimeoutFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import io.workshop.c4s1.ChildWorkflowOneImpl;
import io.workshop.c4s1.ChildWorkflowTwoImpl;
import io.workshop.c4s1.MainWorkflow;
import io.workshop.c4s1.MainWorkflowImpl;

import java.sql.Time;
import java.time.Duration;
import java.util.Collections;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    public static final String taskQueue = "c4s2TaskQueue";

    private static final String workflowId = "c4s2ParentWorkflow";

    public static void main(String[] args) {
        createWorker();

        C4S2Workflow workflow = client.newWorkflowStub(C4S2Workflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                // Set to 30s so we can catch WorkflowFailedException
                .setWorkflowRunTimeout(Duration.ofSeconds(30))
                // Set memo :)
                .setMemo(Collections.singletonMap("mymemo", "mymemovalue"))
                .build());

        WorkflowClient.start(workflow::exec);
        sleep(3);
        // try to cancel
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
        System.out.println("** CLIENT CANCELING!!!");
        untyped.cancel();
        try {
            untyped.getResult(Void.class);
        } catch (WorkflowFailedException e) {
            // wf timed out - TimeoutFailure
            TimeoutFailure timeoutFailure = (TimeoutFailure) e.getCause();
            System.out.println("Timeout: " + timeoutFailure.getTimeoutType().name());
        }
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(C4S2WorkflowImpl.class);

        workerFactory.start();
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
