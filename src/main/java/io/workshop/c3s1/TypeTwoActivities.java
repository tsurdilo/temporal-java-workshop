package io.workshop.c3s1;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
//@ActivityInterface(namePrefix = "B_")
public interface TypeTwoActivities {
    void doSomething(String input);
    void doSomethingElse(String input);
}
