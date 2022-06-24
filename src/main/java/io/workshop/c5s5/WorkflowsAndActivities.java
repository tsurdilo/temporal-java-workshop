package io.workshop.c5s5;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.ChildWorkflowFailure;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;

import java.time.Duration;

public class WorkflowsAndActivities {

    @WorkflowInterface
    public interface LoggerWorkflow {
        @WorkflowMethod
        void exec();
    }

    public static class LoggerWorkflowImpl implements LoggerWorkflow {

        Logger logger = Workflow.getLogger(this.getClass().getName());

        @Override
        public void exec() {

            LoggerChildWorkflow child = Workflow.newChildWorkflowStub(LoggerChildWorkflow.class,
                    ChildWorkflowOptions.newBuilder()
                            .setWorkflowId("childWf")
                            .build());

            logger.info("***** Invoking child workflow");

            try {
                child.execChild();
            } catch (ChildWorkflowFailure childWorkflowFailure) {
                logger.info("***** Child workflow failure");
                ActivityFailure activityFailure = (ActivityFailure) childWorkflowFailure.getCause();
                ApplicationFailure applicationFailure = (ApplicationFailure) activityFailure.getCause();
                logger.info("**** Original failure: " + applicationFailure.getMessage());
            }
        }
    }

    @WorkflowInterface
    public interface LoggerChildWorkflow {
        @WorkflowMethod
        void execChild();
    }

    public static class LoggerChildWorkflowImpl implements LoggerChildWorkflow {

        Logger logger = Workflow.getLogger(this.getClass().getName());

        @Override
        public void execChild() {
            LoggerActivities activities = Workflow.newActivityStub(LoggerActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            // dont do this outside of tests
                            // restrict retries via ScheduleToClose timeout
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setMaximumAttempts(1)
                                    .build())
                            .build());

            logger.info("***** Invoking activity workflow");

            try {
                activities.execActivity();
            } catch (ActivityFailure activityFailure) {
                throw activityFailure;
            }
        }
    }

    @ActivityInterface
    public interface LoggerActivities {
        void execActivity();
    }

    public static class LoggerActivitiesImpl implements LoggerActivities {
        @Override
        public void execActivity() {
            throw new NullPointerException("simulated...");
        }
    }
}
