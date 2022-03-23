package io.workshop.c2s4.childandactivityerrors;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.ChildWorkflowFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;

import java.time.Duration;

public class HandleChildAndActivityErrors {
    public static final String TASK_QUEUE = "handleChildAndActivityErrorsQueue";
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
            // throw a non-retryable application failure
            throw ApplicationFailure.newNonRetryableFailureWithCause("simulated non-retryable failures",
                    "my activity failure", new NullPointerException("simulated activity error"));
        }
    }

    @WorkflowInterface
    public interface MyChildWorkflow {
        @WorkflowMethod
        void exec();
    }

    @WorkflowInterface
    public interface MyWorkflow {
        @WorkflowMethod
        void exec();
    }

    public static class MyChildWorkflowImpl implements MyChildWorkflow {
        private Logger logger = Workflow.getLogger(this.getClass().getName());

        @Override
        public void exec() {
            ActivityOptions activityOptions =
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .build();
            MyActivity activity = Workflow.newActivityStub(MyActivity.class, activityOptions);
            try {
                activity.exec();
            } catch (ActivityFailure e) {
                logger.info("\n**** (child) message: " + e.getMessage());
                logger.info("\n**** (child) cause: " + e.getCause().getClass().getName());
                logger.info("\n**** (child) cause message: " + e.getCause().getMessage());
                if(e.getCause().getCause() != null) {
                    logger.info("\n**** (child) cause->cause: message: " + e.getCause().getCause().getMessage());
                }
                // throw and handle in workflow method
                throw Workflow.wrap(e);
            }
        }
    }

    public static class MyWorkflowImpl implements MyWorkflow {
        private Logger logger = Workflow.getLogger(this.getClass().getName());

        @Override
        public void exec() {

            ChildWorkflowOptions options = ChildWorkflowOptions.newBuilder()
                    .setWorkflowId("MyWorkflowId")
                    .setWorkflowRunTimeout(Duration.ofSeconds(5))
                    .setWorkflowIdReusePolicy(
                            WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE
                    )
                    .build();

            MyChildWorkflow childWorkflow = Workflow.newChildWorkflowStub(MyChildWorkflow.class, options);
            try {
                childWorkflow.exec();
            } catch (ChildWorkflowFailure e) {
                logger.info("\n**** (parent) message: " + e.getMessage());
                logger.info("\n**** (parent) cause: " + e.getCause().getClass().getName());
                logger.info("\n**** (parent) cause message: " + e.getCause().getMessage());
                if(e.getCause().getCause() != null) {
                    logger.info("\n**** (parent) cause->cause: message: " + e.getCause().getCause().getMessage());
                }
                if(e.getCause().getCause().getCause() != null) {
                    logger.info("\n**** (parent) cause->cause->cause: message: " + e.getCause().getCause().getCause().getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);
        worker.registerWorkflowImplementationTypes(MyChildWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MyActivityImpl());
        factory.start();

        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("handlechildandactivityerrors")
                        .build();

        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, workflowOptions);
        workflow.exec();
    }

}
