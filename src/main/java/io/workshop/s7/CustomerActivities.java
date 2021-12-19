package io.workshop.s7;

import io.temporal.activity.ActivityInterface;
import io.workshop.s7.model.Account;
import io.workshop.s7.model.Customer;

@ActivityInterface
public interface CustomerActivities {
    boolean checkCustomerAccount(Customer customer);
    Account getCustomerAccount(Customer customer);
    Account updateCustomerAccount(Account account, int amount, String message);
    void sendBonusEmail(Customer customer, String message);
}
