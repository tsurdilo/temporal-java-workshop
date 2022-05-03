package io.workshop.c4s7;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class MyWorkflowImpl implements MyWorkflow {
    @Override
    public String exec() {
        MyRecoverableActivity activity =
                Workflow.newActivityStub(MyRecoverableActivity.class, ActivityOptions.newBuilder()
                        // limit retries to 3 sec before failing activity
                        .setStartToCloseTimeout(Duration.ofSeconds(3))
                        .build());

        String result = RecoveryUtils.tryOrRecover(activity::recover, activity::exec, "Hello");
        return result;

    }
}
