package io.workshop.c4s2;

import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.netty.util.Timeout;
import io.temporal.client.*;
import io.temporal.failure.CanceledFailure;
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
                // Set to 45s so we can catch WorkflowFailedException
                .setWorkflowRunTimeout(Duration.ofSeconds(45))
                .build());
        try {
            WorkflowClient.start(workflow::exec);
        } catch (WorkflowExecutionAlreadyStarted ee) {
            System.out.println("** WorkflowExecutionAlreadyStarted Exception : " + ee.getMessage());
        } finally {
            try {
                WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
                untyped.getResult(Void.class);
                System.out.println("***** Workflow completed");
            } catch (WorkflowFailedException e) {
                // wf timed out - TimeoutFailure
                if (e.getCause() instanceof TimeoutFailure) {
                    TimeoutFailure timeoutFailure = (TimeoutFailure) e.getCause();
                    System.out.println("** Client - Timeout: " + timeoutFailure.getTimeoutType().name());
                }
            } catch (RuntimeException ee) {
                System.out.println("**** here..." + ee.getMessage());
            }
        }
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(C4S2WorkflowImpl.class);

        workerFactory.start();
    }
}
