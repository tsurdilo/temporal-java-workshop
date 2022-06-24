package io.workshop.c5s3;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class SignalOrderWorkflowImpl implements SignalOrderWorkflow {
    private OrderActivity activity = Workflow.newActivityStub(OrderActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .build());
    private boolean exit = false;
    boolean signalLock = true;

    @Override
    public void exec() {
        Workflow.await(() -> exit);
    }

    @Override
    public void signalA(Models.AModel input) {
//        Workflow.await(() -> signalLock);
//        signalLock = false;
//        try {
//            activity.execActivity(input.getId());
//        } finally {
//            signalLock = true;
//        }
        activity.execActivity(input.getId());
    }

    @Override
    public void signalB(Models.BModel input) {
//        Workflow.await(() -> signalLock);
//        signalLock = false;
//        try {
//            activity.execActivity(input.getId());
//        } finally {
//            signalLock = true;
//        }
        activity.execActivity(input.getId());
    }

    @Override
    public void signalC(Models.CModel input) {
//        Workflow.await(() -> signalLock);
//        signalLock = false;
//        try {
//            activity.execActivity(input.getId());
//        } finally {
//            signalLock = true;
//        }
        activity.execActivity(input.getId());
    }

    @Override
    public void exit() {
        exit = true;
    }
}
