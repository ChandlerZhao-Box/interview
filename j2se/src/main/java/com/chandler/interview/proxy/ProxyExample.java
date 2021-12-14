package com.chandler.interview.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK Proxy动态代理
 */
public class ProxyExample {

    static interface Car {
        void running();
    }

    static class Bus implements Car {
        @Override
        public void running() {
            System.out.println("This is bus running...");
        }
    }

    static class Taxi implements Car {

        @Override
        public void running() {
            System.out.println("This is tax running...");
        }
    }

    static class JDKProxy implements InvocationHandler {
        //代理对象
        private Object target;

        //获取代理对象
        public Object getInstance(Object target) {
            this.target = target;
            //取得代理对象
            return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(), this);
        }

        /**
         * 执行代理方法
         * @param proxy 代理对象
         * @param method 代理方法
         * @param args 方法参数
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("动态代理之前的业务处理");
            //调用方法
            Object result = method.invoke(target, args);
            return result;
        }

    }

    public static void main(String[] args) {
        JDKProxy proxy = new JDKProxy();
        Car car = (Car) proxy.getInstance(new Taxi());
        System.out.println(car instanceof Taxi);
        System.out.println(car instanceof Proxy);

        car.running();
    }
}
