package io.workshop.docssample;

public class Customer {
    private String name;
    private String language;
    private String email;

    // we have to provide a no-arg constructor so our Customer
    // class is serializable by the default temporal json converter
    public Customer() {
    }

    public Customer(String name, String language, String email) {
        this.name = name;
        this.language = language;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
