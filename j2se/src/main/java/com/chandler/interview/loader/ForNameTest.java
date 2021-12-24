package com.chandler.interview.loader;

public class ForNameTest {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName("com.chandler.interview.loader.Person");
        Person p = (Person) clazz.newInstance();
        System.out.println(p.name);
    }

}
