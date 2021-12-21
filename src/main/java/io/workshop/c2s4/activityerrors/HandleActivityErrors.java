package io.workshop.c2s4.activityerrors;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.failure.v1.ApplicationFailureInfoOrBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;

import java.time.Duration;

public class HandleActivityErrors {
    public static final String TASK_QUEUE = "handleWorkflowErrorsTaskQueue";
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    private static final WorkflowClient client = WorkflowClient.newInstance(service);
    private static final WorkerFactory factory = WorkerFactory.newInstance(client);


    @ActivityInterface
    public interface MyActivity {
        void exec();
    }

    public static class MyActivityImpl implements MyActivity {
        @Override
        public void exec() {
            // could also be Activity.wrap(new NullPointerException("simulated activity error"));
            throw new NullPointerException("simulated activity error");


            // for 3.
//            throw ApplicationFailure.newNonRetryableFailureWithCause("simulated non-retryable failures",
//                    "my activity failure", new NullPointerException("simulated activity error"));
        }
    }

    @WorkflowInterface
    public interface MyWorkflow {
        @WorkflowMethod
        void exec();
    }

    public static class MyWorkflowImpl implements MyWorkflow {
        private Logger logger = Workflow.getLogger(this.getClass().getName());

        @Override
        public void exec() {
            ActivityOptions activityOptions =
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    // max attempts set for demo purposes
                                    .setMaximumAttempts(2)
                                    .build())
                            .build();
            MyActivity activity = Workflow.newActivityStub(MyActivity.class, activityOptions);
            try {
                activity.exec();
            } catch (ActivityFailure e) {
                logger.info("\n**** message: " + e.getMessage());
                logger.info("\n**** cause: " + e.getCause().getClass().getName());
                logger.info("\n**** cause message: " + e.getCause().getMessage());
                if(e.getCause().getCause() != null) {
                    logger.info("\n**** cause->cause: message: " + e.getCause().getCause().getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MyActivityImpl());
        factory.start();

        // 1. Catch activity timeout
        catchAndHandle();

        // 2. Retry till workflow timeout
        //retryUntilWorkflowTimeout();

        // 3. Throw a non-retryable failure from activity
        //    even if that failure is not set in retries .doNotRetry
        //handleNonRetryableFailure();
    }

    private static void catchAndHandle() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleactivityerrors")
                        .build();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
        workflow.exec();
    }

    private static void retryUntilWorkflowTimeout() {
        // TODO - remove the activity options retries specifics!
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        // set workflow run timeout, activity will retry until this fires
                        .setWorkflowRunTimeout(Duration.ofSeconds(10))
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleactivityerrors")
                        .build();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
        workflow.exec();
    }

    private static void handleNonRetryableFailure() {
        // TODO - throw the non retryable failure in activity method!
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        // set workflow run timeout, activity will retry until this fires
                        .setWorkflowRunTimeout(Duration.ofSeconds(10))
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleactivityerrors")
                        .build();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
        workflow.exec();
    }
}
