package io.workshop.c4s2;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface C4S2Activities {
    String first();
    boolean second();
}
