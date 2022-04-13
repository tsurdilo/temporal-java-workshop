package io.workshop.c1s2;

import com.google.protobuf.Timestamp;
import io.temporal.api.filter.v1.StartTimeFilter;
import io.temporal.api.filter.v1.WorkflowTypeFilter;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.ListOpenWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListOpenWorkflowExecutionsResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.testing.TestWorkflowRule;
import org.junit.Rule;
import org.junit.Test;

public class ListOpenByStartTest {
    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(ContentLengthWorkflowImpl.class)
                    // set this to true when you want to switch out or mock different activities
//                    .setUseTimeskipping(false)
//                    .setDoNotStart(true)
                    .build();

    @Test
    public void testWithStartTimeFilter() {
//        testWorkflowRule.getWorker().registerActivitiesImplementations(new ContentLengthActivityImpl());
//        testWorkflowRule.getTestEnvironment().start();

        for(int i=0;i<12;i++) {
            ContentLengthWorkflow workflow =
                    testWorkflowRule
                            .getWorkflowClient()
                            .newWorkflowStub(
                                    ContentLengthWorkflow.class,
                                    WorkflowOptions.newBuilder()
                                            .setWorkflowId("Test222-" + i)
                                            .setTaskQueue(testWorkflowRule.getTaskQueue()).build());
            WorkflowClient.start(workflow::execute);
        }
        listOpenWorkflowsByStartTime(1949804799, testWorkflowRule.getTestEnvironment().getWorkflowService(),
                testWorkflowRule.getWorkflowClient());

    }

    @Test
    public void testWithWorkflowTypeFilter() {
//        testWorkflowRule.getWorker().registerActivitiesImplementations(new ContentLengthActivityImpl());
//        testWorkflowRule.getTestEnvironment().start();

        for(int i=0;i<12;i++) {
            ContentLengthWorkflow workflow =
                    testWorkflowRule
                            .getWorkflowClient()
                            .newWorkflowStub(
                                    ContentLengthWorkflow.class,
                                    WorkflowOptions.newBuilder()
                                            .setWorkflowId("Test222-" + i)
                                            .setTaskQueue(testWorkflowRule.getTaskQueue()).build());
            WorkflowClient.start(workflow::execute);
        }
        listOpenWorkflowsByWorkflowType("ContentLengthWorkflow3333", testWorkflowRule.getTestEnvironment().getWorkflowService(),
                testWorkflowRule.getWorkflowClient());

    }

    private void listOpenWorkflowsByStartTime(long seconds, WorkflowServiceStubs service, WorkflowClient client) {
        ListOpenWorkflowExecutionsRequest req = ListOpenWorkflowExecutionsRequest
                .newBuilder()
                .setStartTimeFilter(StartTimeFilter.newBuilder()
                        .setEarliestTime(Timestamp.newBuilder()
                                .setSeconds(seconds)
                                .build())
                        .build())
                .setNamespace(client.getOptions().getNamespace())
                .build();

        ListOpenWorkflowExecutionsResponse res =
                service.blockingStub().listOpenWorkflowExecutions(req);
        for(WorkflowExecutionInfo info : res.getExecutionsList()) {
            System.out.println("******* INFO: " + info.getExecution().getWorkflowId() + " - " +
                    info.getStartTime().getSeconds());
        }
    }

    private void listOpenWorkflowsByWorkflowType(String type, WorkflowServiceStubs service, WorkflowClient client) {
        ListOpenWorkflowExecutionsRequest req = ListOpenWorkflowExecutionsRequest
                .newBuilder()
                .setTypeFilter(WorkflowTypeFilter.newBuilder()
                        .setName(type)
                        .build())
                .setNamespace(client.getOptions().getNamespace())
                .build();

        ListOpenWorkflowExecutionsResponse res =
                service.blockingStub().listOpenWorkflowExecutions(req);
        for(WorkflowExecutionInfo info : res.getExecutionsList()) {
            System.out.println("******* INFO: " + info.getExecution().getWorkflowId() + " - " +
                    info.getStartTime().getSeconds());
        }
    }
}
