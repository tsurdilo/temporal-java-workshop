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
            // 1. show just npe
            throw new NullPointerException("simulated from activity...");
            // 2. show runtime exception with npe as cause
//            throw new RuntimeException("outer", new NullPointerException("inner"));
            // 3. show runtime exception with runtime exception as cause
//            throw new RuntimeException("outer", new RuntimeException("inner"));
        }
    }
}
