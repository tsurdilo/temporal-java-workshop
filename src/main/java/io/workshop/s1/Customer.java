package io.workshop.s1;

public class Customer {
    private String name;
    private String title;
    private String language;

    public Customer() {
    }

    public Customer(String name, String title, String language) {
        this.name = name;
        this.title = title;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
