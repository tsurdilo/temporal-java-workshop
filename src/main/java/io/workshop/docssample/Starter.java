package io.workshop.docssample;

import com.google.common.collect.ImmutableMap;
import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.RetryOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;
import io.workshop.c4s3.WorkflowOneImpl;
import io.workshop.c4s3.WorkflowTwoImpl;

import java.time.Duration;

public class Starter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "greetingTaskQueue";
    private static final String workflowId = "GreetingWorkflowId";

    public static void main(String[] args) {
        createWorker();

        // create typed workflow stub
        GreetingsWorkflow workflow = client.newWorkflowStub(GreetingsWorkflow.class,
                WorkflowOptions.newBuilder()
                        // we have to set the task queue
                        // it is required (same one as our worker)
                        .setTaskQueue(taskQueue)

                        // workflow id is recommended but not a must
                        .setWorkflowId(workflowId)
                        // set timeouts if needed
                        // in order to enable workflow retries
                        // we have to explicitiyl set retry options here
//                        .setRetryOptions(RetryOptions.newBuilder().build())
                        .build());

       // because we want to start and signal our wf in same starter, we have to start exec async
        WorkflowClient.start(workflow::greetCustomer);


        // send signal to our workflow
        Customer customer = new Customer("John", "Spanish", "john@john.com");
        workflow.addCustomer(customer);

        // wait for the workflow to complete
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);

        // here we wait for workflow to complete
        // untypedStub.getResult will block if workflow is still running until wf completes
        String result = untyped.getResult(String.class);
        System.out.println("Result: " + result);
        // note that you can also query workflows after they are completed
        // in this case we can we already got a result
        // lets tryot query it after its done
        // query after wf completed
        Customer queryCustomer = workflow.getCustomer();
        System.out.println("Customer: " + queryCustomer.getName());

    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(
                GreetingWorkflowImpl.class);


//        WorkflowImplementationOptions options =
//                WorkflowImplementationOptions.newBuilder()
//                        // setActivityOptions allows you to set different ActivityOption per activity type
//                        // By default activity type is the name of activity method (with first letter upper
//                        // cased)
//                        .setFailWorkflowExceptionTypes(NullPointerException.class)
//                        .setActivityOptions(
//                                ImmutableMap.of(
//                                        "GetCustomerGreeting",
//                                        ActivityOptions.newBuilder()
//                                                // Set activity exec timeout (including retries)
//                                                .setScheduleToCloseTimeout(Duration.ofSeconds(5))
//                                                // Set activity type specific retries if needed
//                                                .build(),
//                                        "EmailCustomerGreeting",
//                                        ActivityOptions.newBuilder()
//                                                // Set activity exec timeout (single run)
//                                                .setStartToCloseTimeout(Duration.ofSeconds(2))
//                                                .setRetryOptions(
//                                                        RetryOptions.newBuilder()
//                                                                // ActivityTypeB activity type shouldn't retry on NPE
//                                                                .setDoNotRetry(NullPointerException.class.getName())
//                                                                .build())
//                                                .build()))
//                        .build();


        worker.registerActivitiesImplementations(new GreetingActivitiesImpl());

        workerFactory.start();
    }
}
