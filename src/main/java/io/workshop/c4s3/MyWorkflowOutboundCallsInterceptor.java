package io.workshop.c4s3;

import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptorBase;
import io.temporal.internal.sync.WorkflowInternal;
import io.temporal.workflow.CompletablePromise;
import io.temporal.workflow.Functions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyWorkflowOutboundCallsInterceptor extends WorkflowOutboundCallsInterceptorBase {

    public MyWorkflowOutboundCallsInterceptor(WorkflowOutboundCallsInterceptor next) {
        super(next);
    }

    @Override
    public SignalExternalOutput signalExternalWorkflow(SignalExternalInput input) {
        // this is bad hack..dont do at home...
        return new SignalExternalOutput(new Promise<Void>() {
            @Override
            public boolean isCompleted() {
                return true;
            }

            @Override
            public Void get() {
                return null;
            }

            @Override
            public Void cancellableGet() {
                return null;
            }

            @Override
            public Void get(long timeout, TimeUnit unit) throws TimeoutException {
                return null;
            }

            @Override
            public Void cancellableGet(long timeout, TimeUnit unit) throws TimeoutException {
                return null;
            }

            @Override
            public RuntimeException getFailure() {
                return null;
            }

            @Override
            public <U> Promise<U> thenApply(Functions.Func1<? super Void, ? extends U> fn) {
                return null;
            }

            @Override
            public <U> Promise<U> handle(Functions.Func2<? super Void, RuntimeException, ? extends U> fn) {
                return null;
            }

            @Override
            public <U> Promise<U> thenCompose(Functions.Func1<? super Void, ? extends Promise<U>> fn) {
                return null;
            }

            @Override
            public Promise<Void> exceptionally(Functions.Func1<Throwable, ? extends Void> fn) {
                return null;
            }
        });
    }


}
