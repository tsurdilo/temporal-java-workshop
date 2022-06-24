package io.workshop.c5s1;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

public class Workflows {
    @WorkflowInterface
    public interface WorkflowA {
        @WorkflowMethod
        String exec();

        @SignalMethod
        void requestCompleted(String response);
    }

    @WorkflowInterface
    public interface WorkflowB {
        @WorkflowMethod
        String exec();

        @SignalMethod
        void requestCompleted(String response);
    }

    @WorkflowInterface
    public interface WorkflowC {
        @WorkflowMethod
        String exec();

        @SignalMethod
        void requestCompleted(String response);
    }

    @WorkflowInterface
    public interface BulkRequesterWorkflow {
        @WorkflowMethod
        void exec();

        @SignalMethod
        void requestService(String requestorId);
    }
}
