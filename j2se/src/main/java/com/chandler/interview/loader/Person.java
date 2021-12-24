package com.chandler.interview.loader;

public class Person {

    static {
        System.out.println("I am person");
    }

    public String name;

    public Person() {
        this.name = "Chandler";
    }

}
