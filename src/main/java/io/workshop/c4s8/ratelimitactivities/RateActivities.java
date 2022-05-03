package io.workshop.c4s8.ratelimitactivities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface RateActivities {
    String doA(String input);
    String doB(String input);
    String doC(String input);
}
