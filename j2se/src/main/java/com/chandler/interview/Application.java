package com.chandler.interview;

import com.chandler.interview.spring.model.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        List<Person> list = SpringFactoriesLoader.loadFactories(Person.class, Application.class.getClassLoader());
        System.out.println(list.get(0).getRole());
        System.out.println(list.get(1).getRole());

        SpringApplication.run(Application.class, args);
    }
}
