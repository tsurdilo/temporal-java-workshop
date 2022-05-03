package io.workshop.c4s5;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.ChildWorkflowFailure;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.workshop.c2s4.childandactivityerrors.HandleChildAndActivityErrors;

import java.time.Duration;

public class Workflows {

    @WorkflowInterface
    public interface MyWorkflow {
        @WorkflowMethod
        String run();
    }

    @WorkflowInterface
    public interface MyChildWorkflowA {
        @WorkflowMethod
        String run();
    }

    @WorkflowInterface
    public interface MyChildWorkflowB {
        @WorkflowMethod
        String run();
    }

    @WorkflowInterface
    public interface MyChildWorkflowC {
        @WorkflowMethod
        String run();
    }

    public static class MyChildWorkflowCImpl implements MyChildWorkflowC {

        @Override
        public String run() {
            Activities.MyActivities activity = Workflow.newActivityStub(
                    Activities.MyActivities.class, ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            // for sake of demo we can just disable retries here
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setMaximumAttempts(1)
                                    .build())
                            .build());
            return activity.exec();
        }
    }

    public static class MyChildWorkflowBImpl implements MyChildWorkflowB {
        @Override
        public String run() {

            MyChildWorkflowC child = Workflow.newChildWorkflowStub(MyChildWorkflowC.class,
                    ChildWorkflowOptions.newBuilder()
                            .setWorkflowId("ChildWorkflowC")
                            .build());
            return child.run();
        }
    }

    public static class MyChildWorkflowAImpl implements MyChildWorkflowA {
        @Override
        public String run() {

            MyChildWorkflowB child = Workflow.newChildWorkflowStub(MyChildWorkflowB.class,
                    ChildWorkflowOptions.newBuilder()
                            .setWorkflowId("ChildWorkflowB")
                            .build());
            return child.run();
        }
    }

    public static class MyWorkflowImpl implements MyWorkflow {
        @Override
        public String run() {

            MyChildWorkflowA child = Workflow.newChildWorkflowStub(MyChildWorkflowA.class,
                    ChildWorkflowOptions.newBuilder()
                            .setWorkflowId("ChildWorkflowA")
                            .build());
            try {
                return child.run();
            } catch (ChildWorkflowFailure e) {

                ChildWorkflowFailure childBFailure = (ChildWorkflowFailure) e.getCause();
                System.out.println("******* B failure: " + childBFailure.getMessage());

                ChildWorkflowFailure childCFailure = (ChildWorkflowFailure) childBFailure.getCause();
                System.out.println("******* C failure: " + childBFailure.getMessage());

                ActivityFailure activityFailure = (ActivityFailure) childCFailure.getCause();
                System.out.println("******* Activity failure: " + activityFailure.getMessage());

                ApplicationFailure applicationFailure = (ApplicationFailure) activityFailure.getCause();
                System.out.println("******* Application Failure Type: " + applicationFailure.getType());
                System.out.println("******* Application Failure Message: " + applicationFailure.getOriginalMessage());

                // use when showing runtime->npe
//                NullPointerException npe = (NullPointerException) applicationFailure.getCause();
//                System.out.println("****** NullPointerException: " + npe.getMessage()); // inner

                // use when showing runtime->npe
//                RuntimeException re = (RuntimeException) applicationFailure.getCause();
//                System.out.println("****** RuntimeException: " + re.getMessage()); // inner


                throw e;
            }
        }
    }


}
