package io.workshop.c1s1;

/**
 * Simple customer pojo
 */
public class Customer {
    private String name;
    private String title;
    private String languages;
    private int age;

    public Customer() {
    }

    public Customer(String name, String title, String languages, int age) {
        this.name = name;
        this.title = title;
        this.languages = languages;
        this.age = age;
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

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + " Title: " + getTitle() + " Languages: " + getLanguages() + " Age: " + getAge();
    }
}
