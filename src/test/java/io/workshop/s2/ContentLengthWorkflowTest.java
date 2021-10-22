package io.workshop.s2;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.testing.WorkflowReplayer;
import io.workshop.s1.GreetingWorkflowImpl;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ContentLengthWorkflowTest {

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(ContentLengthWorkflowImpl.class)
                    // set this to true when you want to switch out or mock different activities
                    .setDoNotStart(true)
                    .build();

    @Test
    public void testWithRealActivities() {
        testWorkflowRule.getWorker().registerActivitiesImplementations(new ContentLengthActivityImpl());
        testWorkflowRule.getTestEnvironment().start();

        ContentLengthWorkflow workflow =
                testWorkflowRule
                        .getWorkflowClient()
                        .newWorkflowStub(
                                ContentLengthWorkflow.class,
                                WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build());

        ContentLengthInfo info = workflow.execute();

        assertNotNull(info);
        assertNotNull(info.getWebsiteMap());
        assertEquals(1, info.getWebsiteMap().size());

    }

    @Test
    public void testWithMockedActivities() {
        ContentLengthActivity activities =
                mock(ContentLengthActivity.class, withSettings().withoutAnnotations());

        ContentLengthInfo testInfo = new ContentLengthInfo();
        testInfo.add("testsite", 100);

        when(activities.count(anyString())).thenReturn(testInfo);
        testWorkflowRule.getWorker().registerActivitiesImplementations(activities);
        testWorkflowRule.getTestEnvironment().start();

        ContentLengthWorkflow workflow =
                testWorkflowRule
                        .getWorkflowClient()
                        .newWorkflowStub(
                                ContentLengthWorkflow.class,
                                WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build());

        ContentLengthInfo info = workflow.execute();
        assertNotNull(info);
        assertNotNull(info.getWebsiteMap());
        assertEquals(1, info.getWebsiteMap().size());

        assertEquals(100, (int) info.getWebsiteMap().get("testsite"));

    }

    @Test
    public void replayFromHistory() throws Exception {
        WorkflowReplayer.replayWorkflowExecutionFromResource(
                "s2history.json", ContentLengthWorkflowImpl.class);
    }
}
