package io.workshop.c5s3.interceptor;

import io.temporal.activity.ActivityExecutionContext;
import io.temporal.common.interceptors.ActivityInboundCallsInterceptor;
import io.temporal.common.interceptors.ActivityInboundCallsInterceptorBase;

public class OrderActivityInboundCallsInterceptor extends ActivityInboundCallsInterceptorBase {

    private ActivityExecutionContext activityExecutionContext;

    public OrderActivityInboundCallsInterceptor(ActivityInboundCallsInterceptor next) {
        super(next);
    }
}
