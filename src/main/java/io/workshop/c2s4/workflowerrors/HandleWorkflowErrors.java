package io.workshop.c2s4.workflowerrors;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

public class HandleWorkflowErrors {
    public static final String TASK_QUEUE = "handleWorkflowErrorsTaskQueue";
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
            // can also be throw Workflow.wrap(new MyException("...")
            throw new NullPointerException("simulated npe");
        }
    }

    public static void main(String[] args) {
        Worker worker = factory.newWorker(TASK_QUEUE);


        // TODO this is for doFailWorkflow
        WorkflowImplementationOptions options =
                WorkflowImplementationOptions.newBuilder()
                        .setFailWorkflowExceptionTypes(NullPointerException.class)
                        .build();
        worker.registerWorkflowImplementationTypes(options, MyWorkflowImpl.class);

        //worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);

        factory.start();

        // 1. Do nothing, see what happens
        doNothing();

        // 2. Do fail workflow on NPE via WorkflowImplementationOptions
        //doFailWorkflow();

        // 3. Define wf retries and set setDoNotRetry to not fail on npe
        //doFailEvenWithRetries();
    }

    private static void doNothing() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleworkflowerrors")
                        .build();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
        workflow.exec();
    }

    private static void doFailWorkflow() {
        // TODO show WorkflowImplementationOptions
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleworkflowerrors")
                        .build();

        try {
            MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (
                WorkflowException e) {
            System.out.println("** Exception: " + e.getMessage());
            System.out.println("** Cause: " + e.getCause().getClass().getName());
            System.out.println("** Cause message: " + e.getCause().getMessage());
        }
    }

    private static void doFailEvenWithRetries() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleworkflowerrors")
                        .setRetryOptions(RetryOptions.newBuilder()
                                // setting do not retry for NPE!!
                                .setDoNotRetry(NullPointerException.class.getName())
                                .build())
                        .build();

        try {
            MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (
                WorkflowException e) {
            System.out.println("** Exception: " + e.getMessage());
            System.out.println("** Cause: " + e.getCause().getClass().getName());
            System.out.println("** Cause message: " + e.getCause().getMessage());
        }
    }

}
