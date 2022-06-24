package io.workshop.c5s4;

import io.temporal.activity.Activity;

public class C5S4ActivityImpl implements C5S4Activity {
    @Override
    public void runActivity(int next) {
        System.out.println("******* activity: " + next + " - " + Activity.getExecutionContext().getInfo().getActivityId());
    }
}
