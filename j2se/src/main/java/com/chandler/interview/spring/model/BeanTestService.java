package com.chandler.interview.spring.model;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Service;

/**
 * BeanDefinitionRegistryPostProcessor 该接口继承了BeanFactoryPostProcessor接口
 */
@Service
public class BeanTestService implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Dog.class).addPropertyValue("name", "wangwang");
        registry.registerBeanDefinition("dog", definitionBuilder.getBeanDefinition());
        System.out.println("注册bean成功");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Dog dog = beanFactory.getBean(Dog.class);
        System.out.println("dog name: " + dog.getName());
        dog.setName("Xiaobai");
        System.out.println("动态修改bean成功");
    }

}
