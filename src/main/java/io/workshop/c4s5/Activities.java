package io.workshop.c4s5;

import io.temporal.activity.ActivityInterface;
import io.workshop.c2s4.childandactivityerrors.HandleChildAndActivityErrors;

public class Activities {
    @ActivityInterface
    public interface MyActivities {
        String exec();
    }

    public static class MyActivitiesImpl implements Activities.MyActivities {
        @Override
        public String exec() {
            // throw NPE here
            throw new NullPointerException("simulated from activity...");
        }
    }
}
