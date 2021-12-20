package io.workshop.c2s1.actwithretries;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class MyWorkflowImpl implements MyWorkflow {
    @Override
    public void exec() {
        ActivityOptions activityOptions = ActivityOptions.newBuilder()
                .setScheduleToCloseTimeout(Duration.ofMinutes(10))
                .build();
        MyActivity activities = Workflow.newActivityStub(MyActivity.class, activityOptions);

        activities.execActivity();
    }
}
