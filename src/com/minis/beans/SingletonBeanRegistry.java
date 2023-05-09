package com.minis.beans;

public interface SingletonBeanRegistry {
    //注册单例bean
    void registerSingleton(String beanName, Object singletonObject);
    //获取
    Object getSingleton(String beanName);
    //检测是否存在
    boolean containsSingleton(String beanName);
    //查询所有单例bean
    String[] getSingletonNames();
}
