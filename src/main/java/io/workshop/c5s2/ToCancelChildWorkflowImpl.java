package io.workshop.c5s2;

import io.temporal.activity.ActivityCancellationType;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.CanceledFailure;
import io.temporal.workflow.Async;
import io.temporal.workflow.CancellationScope;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ToCancelChildWorkflowImpl implements ToCancelChildWorkflow {
    private Promise<String> activityPromise;
    @Override
    public String execChild(String input) {
        ToCancelActivities activities = Workflow.newActivityStub(ToCancelActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(1))
                        // TODO point out we are waiting cancellation to complete
                        .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                        .setHeartbeatTimeout(Duration.ofSeconds(3))
                        .build());

        activityPromise = Async.function(activities::doSomething, input);

        String result;
        try {
            result = activityPromise.get();
        } catch (ActivityFailure e) {
            System.out.println("In Child, received ActivityFailure.");

            CanceledFailure canceledFailure =
                    (CanceledFailure) e.getCause();
            System.out.println("In Child, activity cancelled failure: " +canceledFailure.getMessage());

            // do some cleanup work if needed
            System.out.println("In Child - performing some cleanup...");
            // rethrow the error to complete cancellation
            throw e;
        }

        return result;
    }
}
