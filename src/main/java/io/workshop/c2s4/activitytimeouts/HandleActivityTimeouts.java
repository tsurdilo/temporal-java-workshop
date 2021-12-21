package io.workshop.c2s4.activitytimeouts;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.ActivityCompletionException;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;

import java.time.Duration;

public class HandleActivityTimeouts {
    public static final String TASK_QUEUE = "handleWorkflowTimeoutsTaskQueue";
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    private static final WorkflowClient client = WorkflowClient.newInstance(service);
    private static final WorkerFactory factory = WorkerFactory.newInstance(client);


    @ActivityInterface
    public interface MyActivity {
        void exec1();
        void exec2();
    }

    public static class MyActivityImpl implements MyActivity {

        @Override
        public void exec1() {
            sleep(3);
        }

        @Override
        public void exec2() {
            ActivityExecutionContext context = Activity.getExecutionContext();
            int hbValue = 0;

            while (true) {
                sleep(2);
                try {
                    context.heartbeat(hbValue++);
                } catch(ActivityCompletionException e) {
                    return;
                }
            }
        }

        private void sleep(int seconds) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }
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
                            // Total time that a workflow is willing to wait for Activity to complete.
                            .setScheduleToCloseTimeout(Duration.ofSeconds(1))
                            .build();
            MyActivity activity = Workflow.newActivityStub(MyActivity.class, activityOptions);
            try {
                activity.exec1();
            } catch (ActivityFailure e) {
                logger.info("\n**** message: " + e.getMessage());
                logger.info("\n**** cause: " + e.getCause().getClass().getName());
            }
        }
    }

    @WorkflowInterface
    public interface MyActivityHeartbeatWorkflow {
        @WorkflowMethod
        void exec();
    }

    public static class MyHeartBeatWorkflowImpl implements MyActivityHeartbeatWorkflow {
        private Logger logger = Workflow.getLogger(this.getClass().getName());

        @Override
        public void exec() {
            ActivityOptions activityOptions =
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(5))
                            // set a small heartbeat timeout to force it
                            .setHeartbeatTimeout(Duration.ofSeconds(1))
                            // for demo dont retry..just fail
                            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                            .build();
            MyActivity activity = Workflow.newActivityStub(MyActivity.class, activityOptions);
            try {
                activity.exec2();
            } catch (RuntimeException e) {
                logger.info("\n**** message: " + e.getMessage());
                logger.info("\n**** cause: " + e.getCause().getClass().getName());
            }
        }
    }

    public static void main(String[] args) {
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);
        worker.registerWorkflowImplementationTypes(MyHeartBeatWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MyActivityImpl());
        factory.start();

        // 1. Catch activity timeout
        catchAndHandle();

        // 2. Retry till workflow timeout
        //retryUntilWorkflowTimeout();

        // 3. Catch heartbeat timeout
        //catchHeartBeatAndHandle();
    }

    private static void catchAndHandle() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleactivitytimeout")
                        .build();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
        workflow.exec();
    }

    private static void retryUntilWorkflowTimeout() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setWorkflowRunTimeout(Duration.ofSeconds(10))
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleactivitytimeout")
                        .build();

        try {
            MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (
                WorkflowException e) {
            System.out.println("** In Client - Exception: " + e.getMessage());
            System.out.println("** In Client - Cause: " + e.getCause().getClass().getName());
            System.out.println("** In Client - Cause message: " + e.getCause().getMessage());
        }
    }

    private static void catchHeartBeatAndHandle() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handleactivityheartbeattimeout")
                        .build();

        try {
            MyActivityHeartbeatWorkflow workflow = client.newWorkflowStub(MyActivityHeartbeatWorkflow.class, workflowOptions);
            workflow.exec();
        } catch (WorkflowException e) {
            System.out.println("** In Client - Exception: " + e.getMessage());
            System.out.println("** In Client - Cause: " + e.getCause().getClass().getName());
            System.out.println("** In Client - Cause message: " + e.getCause().getMessage());
        }
    }
}
