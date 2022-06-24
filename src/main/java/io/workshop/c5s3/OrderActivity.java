package io.workshop.c5s3;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivity {
    void execActivity(String input);
}
