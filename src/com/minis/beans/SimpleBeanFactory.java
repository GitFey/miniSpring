package com.minis.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个简单的bean工厂实现类，同样有注册和get两个功能
 *主要有三个实例域 ：
 * 1.beanDefinitions，是一个泛型列表，存了所有的bean的定义
 * 2.beanNames，是一个字符串列表，存了所有bean的名字
 * 3.singletons，哈希表，key为bean的名字，value为bean实例
 * */
public class SimpleBeanFactory  extends DefaultSingletonBeanRegistry implements BeanFactory,BeanDefinitionRegistry{
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>();

    //beanName和singletons在父级定义了，这里就删掉了

    public SimpleBeanFactory() {
    }

    //getBean，容器的核心方法
    public Object getBean(String beanName) throws BeansException {
        //先尝试直接拿bean实例
        Object singleton = this.getSingleton(beanName);
        //如果此时还没有这个bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            //获取bean的定义
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                throw new BeansException("No bean.");
            } try {
                singleton = Class.forName(beanDefinition.getClassName()).newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            //新注册这个bean实例
            this.registerSingleton(beanName, singleton);
        }
        return singleton;
    }

    //接口新增方法，检查是否存在bean
    public Boolean containsBean(String name) {
        return containsSingleton(name);
    }


    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }


    public boolean isPrototype(String name) {
        return false;
    }


    public Class<?> getType(String name) {
        return null;
    }

    /**
     * 以下方法在拓展bean时新增
     * @param name
     * @param beanDefinition
     */
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
            }
        }
    }
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);

    }

    @Override
    public void removeBeanDefinition(String name) {

    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return false;
    }

    /**
     * 注册,遗留方法
     * */
//    public void registerBeanDefinition(BeanDefinition beanDefinition) {
//        this.beanDefinitions.put(beanDefinition.getId(), beanDefinition);
//    }
}
