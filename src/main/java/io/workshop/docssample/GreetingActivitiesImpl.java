package io.workshop.docssample;

import io.temporal.activity.Activity;

public class GreetingActivitiesImpl implements GreetingActivities {
    @Override
    public String getCustomerGreeting(Customer customer) {

        String greeting = "";
        switch (customer.getLanguage()) {
            case "Spanish":
                greeting = "Hola " + customer.getName();
                break;
            case "French":
                greeting = "Bonjour " + customer.getName();
                break;
            default:
                greeting = "Hello " + customer.getName();
        }

        return greeting;
    }

    @Override
    public void emailCustomerGreeting(Customer customer, String greeting) {
        // here you would create some email client
        sendEmail(customer, greeting);
    }

    private void sendEmail(Customer customer, String email) {
        //...code here that sends the email...for demo
        // we will just print that email was sent

        System.out.println("Email sent to customer: "+ customer.getEmail());
    }
}
