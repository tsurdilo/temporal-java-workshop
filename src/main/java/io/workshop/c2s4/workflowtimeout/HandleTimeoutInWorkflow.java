package io.workshop.c2s4.workflowtimeout;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.time.Duration;

public class HandleTimeoutInWorkflow {

    public static final String TASK_QUEUE = "handleTimeoutTaskQueue";
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    private static final WorkflowClient client = WorkflowClient.newInstance(service);
    private static final WorkerFactory factory = WorkerFactory.newInstance(client);

    @WorkflowInterface
    public interface MyWorkflow {
        @WorkflowMethod
        void exec();
    }

    public static class MyWorkflowImpl implements MyWorkflow {
        @Override
        public void exec() {
            // Just dummy sleep < set run timeout
            Workflow.sleep(Duration.ofSeconds(10));
        }
    }

    public static void main(String[] args) {
            Worker worker = factory.newWorker(TASK_QUEUE);
            worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);

            factory.start();

            startAndTimeout();

            //startAndTimeoutWithWorkflowRetries();

            //startAsCronAndTimeout();

    }

    private static void startAndTimeout() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setWorkflowRunTimeout(Duration.ofSeconds(5))
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("timeoutworkflow")
                        .build();

        try {
            MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (WorkflowException e) {
            System.out.println("** Exception: " + e.getMessage());
            System.out.println("** Cause: " + e.getCause().getClass().getName());
            System.out.println("** Cause message: " + e.getCause().getMessage());
        }
    }

    private static void startAndTimeoutWithWorkflowRetries() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setWorkflowRunTimeout(Duration.ofSeconds(5))
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("timeoutworkflow")
                        .setRetryOptions(RetryOptions.newBuilder()
                                // for demo purposes
                                .setMaximumAttempts(2)
                                .build())
                        .build();

        try {
            MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (WorkflowException e) {
            System.out.println("** Exception: " + e.getMessage());
            System.out.println("** Cause: " + e.getCause().getClass().getName());
            System.out.println("** Cause message: " + e.getCause().getMessage());
        }
    }

    private static void startAsCronAndTimeout() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setWorkflowExecutionTimeout(Duration.ofSeconds(33))
                        .setWorkflowRunTimeout(Duration.ofSeconds(11))
                        .setTaskQueue(TASK_QUEUE)
                        .setCronSchedule("@every 10s")
                        .setWorkflowId("timeoutworkflow")
                        .build();

        try {
            MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (WorkflowException e) {
            System.out.println("** Exception: " + e.getMessage());
            System.out.println("** Cause: " + e.getCause().getClass().getName());
            System.out.println("** Cause message: " + e.getCause().getMessage());
        }
    }
}
