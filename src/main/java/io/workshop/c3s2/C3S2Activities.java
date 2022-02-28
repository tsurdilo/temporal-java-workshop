package io.workshop.c3s2;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface C3S2Activities {
    String doSomething(String input);
    String doSomethingElse(String input);
}
