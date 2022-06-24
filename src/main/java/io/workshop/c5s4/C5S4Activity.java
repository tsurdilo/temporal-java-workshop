package io.workshop.c5s4;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface C5S4Activity {
    void runActivity(int next);
}
