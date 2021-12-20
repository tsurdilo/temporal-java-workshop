package io.workshop.c2s3;

import io.temporal.activity.ActivityInterface;
import io.workshop.c2s3.model.Account;
import io.workshop.c2s3.model.Customer;

@ActivityInterface
public interface CustomerActivities {
    boolean checkCustomerAccount(Customer customer);
    Account getCustomerAccount(Customer customer);
    Account updateCustomerAccount(Account account, int amount, String message);
    void sendBonusEmail(Customer customer, String message);
}
