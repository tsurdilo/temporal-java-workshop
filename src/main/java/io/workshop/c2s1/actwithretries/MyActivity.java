package io.workshop.c2s1.actwithretries;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface MyActivity {
    void execActivity();
}
