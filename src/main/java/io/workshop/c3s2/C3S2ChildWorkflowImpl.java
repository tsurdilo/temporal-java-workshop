package io.workshop.c3s2;

import io.temporal.activity.ActivityOptions;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.workflow.*;

public class C3S2ChildWorkflowImpl implements C3S2ChildWorkflow {
    @Override
    public String execChild(String input) {

        // Typed stub
        C3S2Activities typedStub = Workflow.newActivityStub(
                C3S2Activities.class, ActivityOptions.newBuilder()
                        // ....
                        .build()
        );
        // as local activity
        C3S2Activities localTypedStub = Workflow.newLocalActivityStub(
                C3S2Activities.class, LocalActivityOptions.newBuilder()
                        // ...
                        .build()
        );

        // invoke sync
        String r = typedStub.doSomething("Hello");
        // invoke async (you can also use Async.procedure)
        Promise<String> r1Promise = Async.function(typedStub::doSomething, "Hello");
        String r1 = r1Promise.get();

        // Untyped stub
        ActivityStub untyped2 = Workflow.newUntypedActivityStub(ActivityOptions.newBuilder()
                // ...
                .build());
        // sync
        String r3 = untyped2.execute("", String.class, "Hello");
        // async
        Promise<String> r2Promise = untyped2.executeAsync("", String.class, "Hello");
        String r4 = r2Promise.get();


        // external workflow stub - used to communicate with existing wf exec
        // typed
        C3S2Workflow parentTyped = Workflow.newExternalWorkflowStub(C3S2Workflow.class,
                Workflow.getInfo().getParentWorkflowId().get());
        // signal
        parentTyped.addInput("abc");
        // untyped
        ExternalWorkflowStub parentUntyped = Workflow.newUntypedExternalWorkflowStub(
                Workflow.getInfo().getParentWorkflowId().get());
        // signal
        parentUntyped.signal("addInput", "abc");

        return "done...";
    }
}
