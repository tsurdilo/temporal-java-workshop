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
//                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_REQUEST_CANCEL)
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
            System.out.println("In parent, received ChildWorkflowFailure");
            System.out.println("In parent, cause: " + e.getCause().getClass().getName());
            // do some cleanup work

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
