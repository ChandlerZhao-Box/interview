package com.chandler.interview.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLibProxyExample {

    static class Car {
        public void running() {
            System.out.println("This car is running...");
        }
    }

    static class CGLibProxy implements MethodInterceptor {
        //代理对象
        private Object target;

        public Object getInstance(Object target){
            this.target = target;
            Enhancer enhancer = new Enhancer();
            //设置父类为实力类
            enhancer.setSuperclass(this.target.getClass());
            //回调方法
            enhancer.setCallback(this);
            //创建代理对象
            return enhancer.create();
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            System.out.println("方法调用前的业务处理...");
            Object result = methodProxy.invokeSuper(o, objects);
            return result;
        }
    }

    public static void main(String[] args) {
        CGLibProxy proxy = new CGLibProxy();
        Car car = (Car) proxy.getInstance(new Car());
        car.running();
    }


}
