package io.workshop.c4s4;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface MdcActivity {
    String update();
}
