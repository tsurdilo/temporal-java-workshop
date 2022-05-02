package io.workshop.untypedtest;

import com.google.common.collect.ImmutableMap;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestEnvironmentOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;
import io.temporal.worker.WorkflowImplementationOptions;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;

public class UntypedActivityDifferentTQTest {
    private TestWorkflowEnvironment env;

    @Before
    public void setUp() throws Exception {
//        TestEnvironmentOptions options =
//                TestEnvironmentOptions.newBuilder()
//                        .build();
//        env = TestWorkflowEnvironment.newInstance(options);


                WorkflowImplementationOptions workflowImplementationOptions =
                WorkflowImplementationOptions.newBuilder()
                        // setActivityOptions allows you to set different ActivityOption per activity type
                        // By default activity type is the name of activity method (with first letter upper
                        // cased)
                        .setFailWorkflowExceptionTypes(NullPointerException.class)
                        .setActivityOptions(
                                ImmutableMap.of(
                                        "Run",
                                        ActivityOptions.newBuilder()
                                                // Set activity exec timeout (including retries)
                                                .setScheduleToCloseTimeout(Duration.ofSeconds(5))
                                                .setTaskQueue("t2")
                                                // Set activity type specific retries if needed
                                                .build()))
                        .build();


        env = TestWorkflowEnvironment.newInstance();
        Worker workflowWorker = env.newWorker("t1");
        Worker activityWorker = env.newWorker("t2");

        // Need to register something for workers to start
        workflowWorker.registerWorkflowImplementationTypes(workflowImplementationOptions, MyWorkflowImpl.class);
        activityWorker.registerActivitiesImplementations(new MyActivityImpl());
        env.start();
    }

//    @After
//    public void tearDown() throws Exception {
//        env.close();
//    }

    @Test
    public void testActivityOnDifferentTQ() {
        MyWorkflow workflow = env.getWorkflowClient().newWorkflowStub(MyWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("testWorkflow")
                        .setTaskQueue("t1")
                        .setWorkflowRunTimeout(Duration.ofMinutes(1))
                        .build());

        String res = workflow.exec("Bob");
        assertNotNull(res);
        assertEquals("Bob ... done", res);
    }



    @ActivityInterface
    public interface MyActivity {
        String run(String input);
    }

    public static class MyActivityImpl implements MyActivity {
        @Override
        public String run(String input) {
            return input + " ... done";
        }
    }

    @WorkflowInterface
    public interface MyWorkflow {
        @WorkflowMethod
        String exec(String input);
    }

    public static class MyWorkflowImpl implements MyWorkflow {
        @Override
        public String exec(String input) {
            ActivityOptions options = ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setTaskQueue("t2")
                    .build();

            MyActivity activity = Workflow.newActivityStub(MyActivity.class);
            return activity.run(input);

//            ActivityStub activity = Workflow.newUntypedActivityStub();
//            return activity.execute("Run", String.class, input);
        }
    }
}
