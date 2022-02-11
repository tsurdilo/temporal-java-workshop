package io.workshop.c1s1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.testing.WorkflowReplayer;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GreetingWorkflowTest {

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(GreetingWorkflowImpl.class)
                    .build();

    private Customer testCustomer = new Customer("Elisabeth", "Ms.", "English Spansh", 22);

    @Test
    public void testWorkflow() throws Exception {

        // Get a workflow stub using the same task queue the worker uses.
        GreetingWorkflow workflow =
                testWorkflowRule
                        .getWorkflowClient()
                        .newWorkflowStub(
                                GreetingWorkflow.class,
                                WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build());

        WorkflowClient.start(workflow::greet, testCustomer);

        Thread.sleep( 5 * 1000);
        // Execute a workflow waiting for it to complete.
        //String greeting = workflow.greet(testCustomer);
        //assertEquals("Hello " + testCustomer.getName(), greeting);

        testWorkflowRule.getTestEnvironment().shutdown();

        // Note to self, ADD A SUPER LONG SLEEP to show time advance and run again
    }

    @Test
    public void replayFromHistory() throws Exception {
        WorkflowReplayer.replayWorkflowExecutionFromResource(
                "s1history.json", GreetingWorkflowImpl.class);
    }

//    @Test
    public void getHistoryAndReplay() throws Exception {
        String jsonHistory =
                S1WFUtils.getWorkflowExecutionHistoryAsJson("c1GreetingWorkflow", "87411ad0-5247-454a-91f5-ac182e037f19");
        WorkflowReplayer.replayWorkflowExecution(jsonHistory, GreetingWorkflowImpl.class);
    }
}