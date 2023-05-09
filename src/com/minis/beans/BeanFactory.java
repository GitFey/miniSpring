package com.minis.beans;

public interface BeanFactory {
    /**
     * bean工厂接口，需要实现的是两个功能 ：
     * 1.注册bean
     * 2.根据id获取bean
     * */
    Object getBean(String beanName) throws BeansException;
    void registerBeanDefinition(BeanDefinition beanDefinition);
}