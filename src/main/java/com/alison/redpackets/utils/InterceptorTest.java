package com.alison.redpackets.utils;

public class InterceptorTest {
    public static void main(String[] args) {
        HelloWorld hwProxy = (HelloWorld) InterceptorJdkProxy.bind(new HelloWorldImp(), MyInterceptor.class.getName());
        hwProxy.sayHelloWorld();
    }
}
