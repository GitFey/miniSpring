package com.minis.test;


import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        com.minis.test.AService aService = (com.minis.test.AService)ctx.getBean("aservice");
        aService.sayHello();
    }
}