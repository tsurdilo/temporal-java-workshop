package io.workshop.c5s1;

import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;

import java.util.List;

public class ActivitiesImpl implements Activities {
    @Override
    public void signalBulkRequester(String requesterId) {
        // we dont know if the bulk requester has been started yet or not
        // so we signal via signalwithstart

        WorkflowStub workflow =  Starter.client.newUntypedWorkflowStub(
                Workflows.BulkRequesterWorkflow.class.getSimpleName(),
                WorkflowOptions.newBuilder()
                        .setWorkflowId("BulkProcessor")
                        .setTaskQueue(Starter.taskQueue)
                        .build()
        );

        workflow.signalWithStart("requestService", new Object[]{requesterId}, new Object[] {});
    }

    @Override
    public String callBulkService(List<String> input) {
        input.forEach(c -> System.out.println("Bulk request for " + c));
        return "bulk request completed";
    }
}
