package com.alison.redpackets.utils;

public interface HelloWorld{
    public void sayHelloWorld();
}

class HelloWorldImp implements HelloWorld{
    @Override
    public void sayHelloWorld(){
        System.out.println("Hello World!");
    }
}