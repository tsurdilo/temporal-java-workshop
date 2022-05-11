package io.workshop.c5s2;

import io.temporal.activity.ActivityCancellationType;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
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
                        .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                        .setHeartbeatTimeout(Duration.ofSeconds(5))
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumAttempts(1)
                                .build())
                        .build());

        CancellationScope activityScope =
                Workflow.newCancellationScope(
                        () -> {
                           activityPromise = Async.function(activities::doSomething, input);
                        });

        activityScope.run();
        String result = "";
        try {
            result = activityPromise.get();
        } catch (ActivityFailure e) {
            System.out.println("In Child, received ActivityFailure.");
            System.out.println("In Child, activity cause: " + e.getCause().getClass().getName());

            // do some cleanup work if needed

            // rethrow the error
            throw e;
        }

        return result;
    }
}
