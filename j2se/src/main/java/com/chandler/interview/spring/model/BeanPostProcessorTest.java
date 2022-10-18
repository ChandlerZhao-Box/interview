package com.chandler.interview.spring.model;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

/**
 * BeanPostProcess 在Bean实例化和依赖注入完毕后，在显示调用初始化方法的前后添加我们自己的逻辑。注意是Bean实例化完毕后及依赖注入完成后触发的
 */
@Service
public class BeanPostProcessorTest implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if(bean instanceof Dog) {
//            Dog dog = (Dog) bean;
//            System.out.println("最终为： " + dog.getName());
//            return dog;
//        }
//        return bean;
        System.out.println(beanName);
        return bean;
    }

}
