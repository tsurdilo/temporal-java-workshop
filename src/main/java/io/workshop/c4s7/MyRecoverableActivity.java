package io.workshop.c4s7;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface MyRecoverableActivity extends RecoveryUtils.RecoverableActivity<String, String> {
    @ActivityMethod
    String exec(String input);
}
