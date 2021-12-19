package io.workshop.c2s1.model;

public class Account {
    private String accountNum;
    private String customerNum;
    private String message;
    private Customer customer;
    private int amount = 0;

    public Account() {}

    public Account(String accountNum, String customerNum, String message, Customer customer) {
        this.accountNum = accountNum;
        this.customerNum = customerNum;
        this.message = message;
        this.customer = customer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void updateAmount(int amount, String message) {
        this.message = message;
        this.amount += amount;
    }
}