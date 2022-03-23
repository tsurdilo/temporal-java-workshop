package io.workshop.c4s4;

import io.temporal.activity.Activity;
import org.slf4j.MDC;

public class MdcActivityImpl implements MdcActivity {
    @Override
    public String update() {
        return "fromactivity1";
    }
}
