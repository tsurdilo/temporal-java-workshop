package io.workshop.c2s1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;
import io.workshop.c1s1.Customer;
import io.workshop.c1s1.GreetingWorkflow;
import io.workshop.c1s1.GreetingWorkflowImpl;
import io.workshop.c1s2.*;
import io.workshop.c1s3.ChildWorkflowImpl;
import io.workshop.c1s3.ParentWorkflow;
import io.workshop.c1s3.ParentWorkflowImpl;
import io.workshop.c2s1.actwithretries.MyActivityImpl;
import io.workshop.c2s1.actwithretries.MyWorkflow;
import io.workshop.c2s1.actwithretries.MyWorkflowImpl;
import io.workshop.c2s1.cronwf.CronWorkflow;
import io.workshop.c2s1.cronwf.CronWorkflowImpl;

import java.time.Duration;

public class SetupWorkflows {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // tast queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c2s3TaskQueue";

    // Set up for c2s1 (client apis)
    public static void main(String[] args) {
        setupWorker();
        setupFailedWorkflows();
        setupCompletedWorkflows();
        setupRunningWorkflows();
        setupCronWorkflows();
        setupWorkflowWithActivityRetries();
    }

    private static void setupFailedWorkflows() {
        // Setup a number of failed workflows
        for(int i = 0; i < 5; i++) {
            GreetingWorkflow workflow = client.newWorkflowStub(
                    GreetingWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("greetingWorkflow-" + i)
                            .setTaskQueue(taskQueue)
                            .build()
            );
            Customer customer = new Customer("Elisabeth", "Ms", "English Spanish", 20);

            WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
            // will cause wf to fail (npe)
            untyped.signalWithStart("setCustomer", new Object[] {customer}, null);
        }
    }

    private static void setupCompletedWorkflows() {
        // Set up couple of completed workflows
        for(int i = 0; i < 5; i++) {
            ContentLengthWorkflow workflow = S2WFUtils.client.newWorkflowStub(
                    ContentLengthWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("contentworkflow-" + i)
                            .setTaskQueue(taskQueue)
                            .build()
            );
            workflow.execute();
        }
    }

    private static void setupRunningWorkflows() {
        for(int i = 0; i < 2; i++) {
            ParentWorkflow workflow = client.newWorkflowStub(
                    ParentWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("parentWorkflow-" + i)
                            .setTaskQueue(taskQueue)
                            .build()
            );
            WorkflowClient.start(workflow::executeParent, i);
        }
    }

    private static void setupCronWorkflows() {
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setWorkflowId("CronWorkflow")
                        .setTaskQueue(taskQueue)
                        .setCronSchedule("@every 20s")
                        // at which time to stop the cron
                        .setWorkflowExecutionTimeout(Duration.ofMinutes(5))
                        .build();

        CronWorkflow cronWorkflow = client.newWorkflowStub(CronWorkflow.class, workflowOptions);
        WorkflowClient.start(cronWorkflow::exec);
    }

    private static void setupWorkflowWithActivityRetries() {
        MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("WfWithActivityRetries")
                        .setTaskQueue(taskQueue)
                        .build());

        workflow.exec();
    }

    private static void setupWorker() {
        WorkflowImplementationOptions workflowImplementationOptions =
                WorkflowImplementationOptions.newBuilder()
                        .setFailWorkflowExceptionTypes(NullPointerException.class)
                        .build();

        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        // Can be called multiple times

        // Greeting wf
        worker.registerWorkflowImplementationTypes(workflowImplementationOptions, GreetingWorkflowImpl.class);

        // Content length wf and activities
        worker.registerWorkflowImplementationTypes(ContentLengthWorkflowImpl.class);
        worker.registerActivitiesImplementations(new ContentLengthActivityImpl());

        // parent and child wf
        worker.registerWorkflowImplementationTypes(ParentWorkflowImpl.class,
                ChildWorkflowImpl.class);

        // cron wf
        worker.registerWorkflowImplementationTypes(CronWorkflowImpl.class);

        // activity retries wf
        worker.registerWorkflowImplementationTypes(MyWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MyActivityImpl());

        workerFactory.start();
    }
}
