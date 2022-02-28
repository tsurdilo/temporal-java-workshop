package io.workshop.c3s2;

import io.temporal.workflow.*;

public class C3S2WorkflowImpl implements C3S2Workflow {
    @Override
    public String execute(String input) {

        // Child workflow stubs

        // 1. Typed
        C3S2ChildWorkflow child = Workflow.newChildWorkflowStub(C3S2ChildWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                        // ....
                        .build());
        // sync
        String r1 = child.execChild("Hello");

        // async (look also at Async.procedure)
        Promise<String> greeting = Async.function(child::execChild, "Hello");
        // wait for results
        String r2 =  greeting.get();

        // 2. Untyped
        ChildWorkflowStub child2 = Workflow.newUntypedChildWorkflowStub("C3S2ChildWorkflow",
                ChildWorkflowOptions.newBuilder()
                        // ....
                        .build());

        // start sync
        String r3 = child2.execute(String.class, "Hello");

        // start async
        Promise<String> r3Promise = child2.executeAsync(String.class, "Hello");
        // wait for results later
        String r4 = r3Promise.get();

        // signal
        child2.signal("signalName", "Hello2");
        // CANNOT QUERY and SIGNALWITHSTART FROM INSIDE WORKFLOW


        return "nothing...";

    }

    @Override
    public void addInput(String input) {
        // ...
    }

    @Override
    public String getInput() {
        return null;
    }
}
