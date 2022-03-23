package io.workshop.c4s4;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.MDC;

import java.time.Duration;

public class MdcWorkflowImpl implements MdcWorkflow {
    @Override
    public String exec() {
        MDC.put("test", "workflow123");
        MdcActivity activity = Workflow.newActivityStub(MdcActivity.class,
                ActivityOptions.newBuilder()
                        // set here if you want to overwrite one set in WorkflowClientOptions and/or WorkflowOptions
//                        .setContextPropagators(Collections.singletonList(Starter.propagator))
                        .setStartToCloseTimeout(Duration.ofSeconds(1))
                        .build());
        MDC.put("test", activity.update());
        return MDC.get("test");
    }
}
