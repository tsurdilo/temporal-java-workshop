package io.workshop.c1s3;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

public class ParentWorkflowImpl implements ParentWorkflow {

    @Override
    public void executeParent(int count) {
        invokeChildWaitForResults(count);

        //invokeChildDontWaitForResults(count);

        // Note: child workflows can be invoked in parallel as well, see Activity demos in s2 package
    }

    private void invokeChildWaitForResults(int count) {
        ChildWorkflowOptions childWorkflowOptions =
                ChildWorkflowOptions.newBuilder()
                        .setWorkflowId("childWorkflow-" + count)
                        // Child workflow can be invoked on a different task queue
                        // by default its the parent task queue
                        //.setTaskQueue("someOtherTaskQueue")

                        // parent close policy defines what happens to child exec when
                        // parent exec finishes. default is terminate the child
                        // .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)

                        // Child workflows can have own execution and run timeouts!
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(10))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(4))

                        // Child workflows can be scheduled (cron)
                        //.setCronSchedule("@every 10s")
                        .build();

        // Get the child workflow stub
        ChildWorkflow child = Workflow.newChildWorkflowStub(ChildWorkflow.class, childWorkflowOptions);
        // invoke child workflow and wait for it to complete
        child.executeChild(count);
    }

    private void invokeChildDontWaitForResults(int count) {
        ChildWorkflowOptions childWorkflowOptions =
                ChildWorkflowOptions.newBuilder()
                        .setWorkflowId("childWorkflow-" + count)
                        // Child workflow can be invoked on a different task queue
                        // by default its the parent task queue
                        //.setTaskQueue("someOtherTaskQueue")

                        // parent close policy defines what happens to child exec when
                        // parent exec finishes. default is terminate the child
                        //.setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)
                        .build();

        // Get the child workflow stub
        ChildWorkflow child = Workflow.newChildWorkflowStub(ChildWorkflow.class, childWorkflowOptions);


        // Start the child workflow async..we dont expect result
        Async.procedure(child::executeChild, count);

        // Get the child workflow execution promise
        Promise<WorkflowExecution> childExecution = Workflow.getWorkflowExecution(child);

        // Wait until child workflow is started
        childExecution.get();

        // Note: Since this .get only waits for child to start, and parent workflow completes
        // right after see in web ui that child was terminated (as it sleeps for 3 seconds)
        // Show how this changes when parent close policy is abandon
    }
}
