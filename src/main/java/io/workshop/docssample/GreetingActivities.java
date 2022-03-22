package io.workshop.docssample;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface GreetingActivities {
    String getCustomerGreeting(Customer customer);
    void emailCustomerGreeting(Customer customer, String greeting);
}
