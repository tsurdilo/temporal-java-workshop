package io.workshop.c2s3.model;

import java.time.Duration;

public class Customer {
    private String accountNum;
    private String name;
    private String email;
    private String customerType;
    private Duration demoWaitDuration;

    public Customer() {}

    public Customer(String accountNum, String name, String email, String customerType, Duration demoWaitDuration) {
        this.accountNum = accountNum;
        this.name = name;
        this.email = email;
        this.customerType = customerType;
        this.demoWaitDuration = demoWaitDuration;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Duration getDemoWaitDuration() {
        return demoWaitDuration;
    }

    public void setDemoWaitDuration(Duration demoWaitDuration) {
        this.demoWaitDuration = demoWaitDuration;
    }
}

