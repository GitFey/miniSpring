package com.minis.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 一个简单的bean工厂实现类，同样有注册和get两个功能
 *主要有三个实例域 ：
 * 1.beanDefinitions，是一个泛型列表，存了所有的bean的定义
 * 2.beanNames，是一个字符串列表，存了所有bean的名字
 * 3.singletons，哈希表，key为bean的名字，value为bean实例
 * */
public class SimpleBeanFactory implements BeanFactory{
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private List<String> beanNames = new ArrayList<>();
    private Map<String, Object> singletons = new HashMap<>();
    public SimpleBeanFactory() {
    }

    //getBean，容器的核心方法
    public Object getBean(String beanName) throws BeansException {
        //先尝试直接拿Bean实例
        Object singleton = singletons.get(beanName);
        //如果此时还没有这个Bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            //看name有没有被注册
            int i = beanNames.indexOf(beanName);
            if (i == -1) {
                throw new BeansException();
            }
            else {
                //获取Bean的定义
                BeanDefinition beanDefinition = beanDefinitions.get(i);
                try {
                    singleton = Class.forName(beanDefinition.getClassName()).newInstance();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                //Bean实例
                singletons.put(beanDefinition.getId(), singleton);
            }
        }
        return singleton;
    }
    /**
     * 注册
     * */
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.add(beanDefinition);
        this.beanNames.add(beanDefinition.getId());
    }
}