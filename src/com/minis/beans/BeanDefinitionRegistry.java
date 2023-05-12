package com.minis.beans;

/**
 * 拓展bean时新增 ：
 * 目的是为了统一管理BeanDefinition
 * 它类似于一个存放 BeanDefinition 的仓库，可以存放、移除、获取及判断 BeanDefinition 对象。
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition bd);
    void removeBeanDefinition(String name);
    BeanDefinition getBeanDefinition(String name);
    boolean containsBeanDefinition(String name);
}
