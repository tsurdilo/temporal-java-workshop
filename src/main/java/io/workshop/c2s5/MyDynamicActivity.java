package io.workshop.c2s5;

import io.temporal.activity.Activity;
import io.temporal.activity.DynamicActivity;
import io.temporal.common.converter.EncodedValues;

public class MyDynamicActivity implements DynamicActivity {
    @Override
    public Object execute(EncodedValues args) {
        String activityType = Activity.getExecutionContext().getInfo().getActivityType();

        return "From " + activityType + " " + args.get(0, String.class) + " " + args.get(1, String.class);
    }
}
