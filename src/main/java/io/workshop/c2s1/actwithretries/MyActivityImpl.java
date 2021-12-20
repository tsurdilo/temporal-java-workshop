package io.workshop.c2s1.actwithretries;

import io.temporal.activity.Activity;

public class MyActivityImpl implements MyActivity {
    @Override
    public void execActivity() {
        // force retry after some time
        try {
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw Activity.wrap(new NullPointerException("simulated..."));
    }
}
