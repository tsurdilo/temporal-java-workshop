package io.workshop.c2s5;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.converter.EncodedValues;
import io.temporal.workflow.*;

import java.time.Duration;

public class MyDynamicWorkflow implements DynamicWorkflow {
    private String name;

    @Override
    public Object execute(EncodedValues args) {
        String greeting = args.get(0, String.class);
        String type = Workflow.getInfo().getWorkflowType();

        // Register dynamic signal handler
        // can check signal name to do different logic
        Workflow.registerListener(
                (DynamicSignalHandler)
                        (signalName, encodedArgs) -> name = encodedArgs.get(0, String.class));

        // Register dynamic query handler
        // can check query type to do different logic..for demo just return name
        Workflow.registerListener(
                (DynamicQueryHandler)
                        (queryType, encodedArgs) -> name);


        // Define activity options and get ActivityStub
        ActivityStub activity =
                Workflow.newUntypedActivityStub(
                        ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(10)).build());
        // Execute the dynamic Activity. Note that the provided Activity name is not
        // explicitly registered with the Worker
        String result = activity.execute("DynamicACT", String.class, greeting, name, type);

        // Return results
        return result;
    }
}
