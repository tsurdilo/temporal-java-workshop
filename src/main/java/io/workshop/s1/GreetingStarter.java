package io.workshop.s1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.workshop.s1.WFUtils.client;
import static io.workshop.s1.WFUtils.taskQueue;

public class GreetingStarter {

    // Dummy customers
    private static final Customer customer1 = new Customer("Elisabeth", "Ms", "English Spanish", 20);
    private static final Customer customer2 = new Customer("Michael", "Mr", "Chinese Italian", 32);
    private static final Customer customer3 = new Customer("John", "Mr", "German English", 19);
    private static final Customer customer4 = new Customer("Ivan", "Mr", "Spanish Russian", 44);
    private static final Customer customer5 = new Customer("Donna", "Ms", "English", 50);
    private static final List<Customer> allCustomers = Arrays.asList(customer1, customer2,
            customer3, customer4, customer5);

    // Domain-specific workflow id
    private static final String workflowId = "c1GreetingWorkflow";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //startTypedAndWaitForResult();

        //startAsyncExpectWaitForResult();

        //startAsyncDontExpectWaitForResult();

        //startAsyncGetResultInAnotherProcessTyped();

        //startAsyncGetResultInAnotherProcessUnTyped();

        signalWithStart();
        // show that second time it will just signal -- good for lazy creation
        // arguments ignored

        //startAsCronAsync();

        //startAndTerminate();

        //startAndSignal();

        //startAndQueryRunning();

        //startAndQueryCompleted();

        //startWithSearchAttributes();

        System.exit(0);
    }

    private static void startTypedAndWaitForResult() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        String greeting = workflow.greet(customer1);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);
    }

    private static void startAsyncExpectWaitForResult() throws ExecutionException, InterruptedException {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        CompletableFuture<String> resultFuture = WorkflowClient.execute(workflow::greet, customer1);

        printWorkflowStatus();

        String greeting = resultFuture.get();
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);
    }

    public static void startAsyncDontExpectWaitForResult() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        // does not block
        WorkflowClient.start(workflow::greet, customer1);

        printWorkflowStatus();

        // still can connect to WF and get result using untyped:
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);

        String greeting = untyped.getResult(String.class);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);
    }

    public static void startAsyncGetResultInAnotherProcessTyped() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        // this is the default
                        //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY);
                        .build()
        );

        // start async, not blocking
        WorkflowClient.start(workflow::greet, customer1);

        printWorkflowStatus();

        // connect to running workflow using typed stub
        // note - this is contengent on workflowIdReusePolicy (don't set to ALLOW_DUPLICATES :))
        GreetingWorkflow workflow1 = client.newWorkflowStub(GreetingWorkflow.class, workflowId);
        // Note that this input is ignored (workflow stub connected with already existing one)
        String greeting = workflow1.greet(customer2);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);
    }

    public static void startAsyncGetResultInAnotherProcessUnTyped() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        // 1.1 cron
                        //.setCronSchedule("@every 10s")
                        .build()
        );

        // start async, not blocking
        WorkflowClient.start(workflow::greet, customer1);

        printWorkflowStatus();

        // from another process, untyped
        // inputs workflowid, runid, workflow type
        // WorkflowStub is a client side stub to a single workflow instance.
        WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowId, Optional.empty(), Optional.empty());

        String greeting = existingUntyped.getResult(String.class);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);
    }

    public static void signalWithStart() {
        // WorkflowStub is a client-side stub to a single workflow instance
        WorkflowStub untypedWorkflowStub = client.newUntypedWorkflowStub("GreetingWorkflow",
        WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
//                .setRetryOptions(RetryOptions.newBuilder()
//                        .setMaximumAttempts(3)
//                        .build())
                .build());

        untypedWorkflowStub.signalWithStart("setCustomer", new Object[] {customer1}, null);

        printWorkflowStatus();

        try {
            String greeting = untypedWorkflowStub.getResult(String.class);
            printWorkflowStatus();
            System.out.println("Greeting: " + greeting);
        } catch(WorkflowFailedException e) {
            System.out.println("Workflow failed: " + e.getCause().getMessage());
            printWorkflowStatus();
        }
    }

    public static void startAsCronAsync() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .setCronSchedule("@every 10s")
                        .build()
        );

        // start async, not blocking
        WorkflowClient.start(workflow::greet, customer1);
    }

    public static void startAndTerminate() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        // start async, not blocking
        WorkflowClient.start(workflow::greet, customer1);

        // Terminate it
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
        untyped.terminate("Workshop reasons...");

        printWorkflowStatus();
    }

    public static void startAndSignal() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        // start async, not blocking
        WorkflowClient.start(workflow::greet, customer1);

        // call signal method
        workflow.setCustomer(customer2);

        // connect to WF and get result using untyped:
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);

        String greeting = untyped.getResult(String.class);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);

    }

    public static void startAndQueryRunning() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        // start async, not blocking
        WorkflowClient.start(workflow::greet, customer1);

        // call signal method
        Customer queriedCustomer = workflow.getCustomer();
        System.out.println("Query: " + queriedCustomer.getName());


        // connect to WF and get result using untyped:
        WorkflowStub untyped = WorkflowStub.fromTyped(workflow);

        String greeting = untyped.getResult(String.class);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);

    }

    public static void startAndQueryCompleted() {
        // Workflow client stub
        // Stub is per per workflow instance - create new stub for each
        GreetingWorkflow workflow = client.newWorkflowStub(
                GreetingWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        //.setWorkflowExecutionTimeout(Duration.ofSeconds(20))
                        //.setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setTaskQueue(taskQueue)
                        .build()
        );

        // start sync and wait for completion
        String greeting = workflow.greet(customer1);
        printWorkflowStatus();
        System.out.println("Greeting: " + greeting);

        // query completed workflow
        Customer queriedCustomer = workflow.getCustomer();
        System.out.println("Query: " + queriedCustomer.getName());
    }


    public static void startWithSearchAttributes() {
       // Start workflows (async) for all customers
        for(Customer customer : allCustomers) {
            Map<String, Object> searchAttributes = new HashMap<>();
            searchAttributes.put("CustomerTitle", customer.getTitle());
            searchAttributes.put("CustomerLanguages", customer.getLanguages());
            searchAttributes.put("CustomerAge", customer.getAge());

            WorkflowOptions newCustomerWorkflowOptions =
                    WorkflowOptions.newBuilder()
                            .setTaskQueue(taskQueue)
                            // set the search attributes for this customer workflow
                            .setSearchAttributes(searchAttributes)
                            .build();

            GreetingWorkflow workflow = client.newWorkflowStub(GreetingWorkflow.class, newCustomerWorkflowOptions);

            WorkflowClient.start(workflow::greet, customer);
        }
    }

    private static void printWorkflowStatus() {
        System.out.println("Workflow Status: " + WFUtils.getWorkflowStatus(client, workflowId));
    }
}
