package com.minis.test;

import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;
import com.minis.web.RequestMapping;


public class HelloWorldBean {
    @RequestMapping("/test1")
    public String doTest1() {
        return "test 1, hello world!";
    }
    @RequestMapping("/test2")
    public String doTest2() {
        return "test 2, hello world!";
    }

    @RequestMapping("/test3")
    public String doTest3() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("annotationTest.xml");
        AService aService;
        try {
            aService = (AService)ctx.getBean("aservice");
            aService.sayHello();
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return "aService.sayHello();";
    }
}