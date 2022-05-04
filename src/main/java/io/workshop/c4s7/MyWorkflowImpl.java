package io.workshop.c4s7;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class MyWorkflowImpl implements MyWorkflow {
    @Override
    public String exec() {
        MyRecoverableActivity activity =
                Workflow.newActivityStub(MyRecoverableActivity.class, ActivityOptions.newBuilder()
                        // just for demo limiting to 3 retries
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumAttempts(3)
                                .build())
                        .build());

        String result = RecoveryUtils.tryOrRecover(activity::recover, activity::exec, "Hello");
        return result;

    }
}
