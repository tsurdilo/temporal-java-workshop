package io.workshop.docssample;

import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class GreetingWorkflowImpl implements  GreetingsWorkflow {
    // A workflow can have state
    private Customer customer;

    @Override
    public String greetCustomer() {

        throw ApplicationFailure.newNonRetryableFailure("custom message",
                NullPointerException.class.getName(), "custom details");

        // Wait until we get a new customer via Signal, and then greet them
        // Wait until we get a customer via signal
//        Workflow.await(() -> customer != null);
//
//        // at this point our customer is not null
//        GreetingActivities activities = Workflow.newActivityStub(GreetingActivities.class,
//                // Note these activity options apply for ALL of our activities
//                // if you want per-activity type options use WorkflowImplementationOptions
//                // Note that if you do define per-activity options via WorkflowImplementationOptions
//                // setting them specifically here will overwrite that setting
//                ActivityOptions.newBuilder()
//                        .setStartToCloseTimeout(Duration.ofSeconds(5))
//                        // if task queue not set, it will be same tq as what workflow uses
//                        // .setTaskQueue("...")
//
//                        // if retry options is not set explicitly activities have
//                        // a default retry options
//
//                        // to disable retries, set max attempts to 1
//                        // do not use maxAttempts to limit retry options (other than
//                        // disabling them with 1), use timeouts instead (scheduleToStartTimeout)
////                        .setRetryOptions(RetryOptions.newBuilder()
////                                .setMaximumAttempts(1)
////                                .build())
//                        .build());
//
//        // if we wanted to use activity options defined in Starter
////        GreetingActivities activities2 = Workflow.newActivityStub(GreetingActivities.class);
//
//
//        try {
//            String finalGreeting = activities.getCustomerGreeting(customer);
//            activities.emailCustomerGreeting(customer, finalGreeting);
//            return finalGreeting;
//        } catch (ActivityFailure failure) {
//            // Activity invocations will ALWAYS throw ActivityFailure
//            // the original failure (such as NPE) is going to be in its "cause"
//            // Note that ActivityFailure is ONLY delivered AFTER all the retries are done
//
//            // you can handle the error here buy doing maybe compensation or call another activy...
//
//            // for our sample we just return some custom error string
//            return "Unable to send final greeting";
//        }
    }

    @Override
    public Customer getCustomer() {
       return customer;
    }

    @Override
    public void addCustomer(Customer customer) {
        this.customer = customer;
    }
}
