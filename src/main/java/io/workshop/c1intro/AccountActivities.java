package io.workshop.c1intro;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AccountActivities {
    String notify(String accountId);
}
