package io.workshop.c1s2;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ContentLengthActivity {
    // @ActivityMethod(name = "abc") --> not needed, default activity type is name of method
    ContentLengthInfo count(String url);
}
