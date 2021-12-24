package com.chandler.interview.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassLoaderTest {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = ClassLoaderTest.class.getClassLoader().loadClass("com.chandler.interview.loader.Person");
        Constructor constructor = clazz.getConstructors()[0];
        Person p = (Person) constructor.newInstance();
        System.out.println(p.name);
    }
}
