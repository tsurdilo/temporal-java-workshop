package io.workshop.c4s8.ratelimitactivities;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MyWorkflowImpl implements MyWorkflow {
    private String result = "";

    @Override
    public String exec(String input) {
        RateActivities activities = Workflow.newActivityStub(RateActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(20))
                        .build());

        // start activities in parallel
        List<Promise<String>> promiseList = new ArrayList<>();
        promiseList.add(Async.function(activities::doA, input));
        promiseList.add(Async.function(activities::doB, input));
        promiseList.add(Async.function(activities::doC, input));

        // Invoke all activities in parallel. Wait for all to complete
        Promise.allOf(promiseList).get();

        // Loop through promises and get results (note error handling omitted)
        for (Promise<String> promise : promiseList) {
            if (promise.getFailure() == null) {
                result += "\n" + promise.get();
            }
        }

        return result;
    }
}
