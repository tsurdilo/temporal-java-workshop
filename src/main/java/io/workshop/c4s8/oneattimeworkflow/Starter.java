package io.workshop.c4s8.oneattimeworkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import io.workshop.c4s8.ratelimitactivities.MyWorkflow;
import io.workshop.c4s8.ratelimitactivities.MyWorkflowImpl;
import io.workshop.c4s8.ratelimitactivities.RateActivitiesImpl;

import java.time.Duration;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c4s8RateTaskQueue";
    public static final String workflowId = "OneAtTimeWorkflow";

    public static void main(String[] args) {
        createWorker();

        RatedWorkflow workflow = client.newWorkflowStub(RatedWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .build());

        // Start async
        WorkflowClient.start(workflow::oneAtTime, null);

        // start sending signals to workflow to execute child workflows one at time
        for(int i = 0; i < 30; i++) {
            workflow.toInvoke(new ToInvoke("MyChildWorkflow", "Child" + (i + 1), "Hello " + (i + 1)));
            // short sleep..remember the UnhandledCommand? Our workflow has to continueAsNew
            sleep(500);
        }

        sleep(40 * 1000);
        // send exit signal
        workflow.exit();
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);

        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(RatedWorkflowImpl.class,
                MyChildWorkflowImpl.class);
        workerFactory.start();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
