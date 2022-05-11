package io.workshop.c5s2;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ToCancelActivities {
    String doSomething(String input);
}
