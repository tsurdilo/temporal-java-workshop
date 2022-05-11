package io.workshop.c5s2;

import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.failure.ChildWorkflowFailure;
import io.temporal.workflow.*;

public class ToCancelWorkflowImpl implements ToCancelWorkflow {
    private Promise<String> childPromise;
    CancellationScope childScope;
    @Override
    public String exec(String input) {
        ToCancelChildWorkflow child = Workflow.newChildWorkflowStub(ToCancelChildWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                        .setWorkflowId("ToCancelChild")
                        .setCancellationType(ChildWorkflowCancellationType.WAIT_CANCELLATION_COMPLETED)
                        .build());

        childScope =
                Workflow.newCancellationScope(
                        () -> {
                            childPromise = Async.function(child::execChild, input);
                        });

        childScope.run();

        String result = "";
        try {
            result = childPromise.get();
        } catch ( ChildWorkflowFailure e) {
            System.out.println("In Parent, received ChildWorkflowFailure");
            System.out.println("In Parent, cause: " + e.getCause().getClass().getName());
            // do some cleanup work
            System.out.println("In Parent - performing some cleanup...");
            // return all cancelled
           return "all cancelled";
        }

        return result;
    }

    @Override
    public void doCancel() {
        childScope.cancel();
    }
}
