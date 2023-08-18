package com.minis.test;


import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;

/**
 * 测试文件，要做的事 ：
 * 1.通过annotationTest.xml读取配置文件
 * 2.获取bbs、aservice、baseservice三个bean文件
 * 3.指定获取aservice的bean文件，调用其sayhello方法
 */
public class Test1 {
    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("annotationTest.xml");
        AService aService;
        try {
            aService = (AService)ctx.getBean("aservice");
            aService.sayHello();
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}