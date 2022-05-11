package io.workshop.c5s2;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCanceledException;

public class ToCancelActivitiesImpl implements ToCancelActivities {
    @Override
    public String doSomething(String input) {

        ActivityExecutionContext context = Activity.getExecutionContext();

        for(int i=0;i<100;i++) {
            sleep(1);
            try {
                context.heartbeat(i);
            } catch (ActivityCanceledException e) {
                System.out.println("In Activity - received cancellation request.");
                // do some "cleanup" if needed
                System.out.println("In Activity - performing some cleanup...");
                // rethrow the error
                throw e;
            }
        }

        return "activity completed";
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
