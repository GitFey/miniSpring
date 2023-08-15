package com.minis.beans.factory;

import java.util.Map;

import com.minis.beans.BeansException;

/**
 * 此接口可以将 Factory 内部管理的 Bean 作为一个集合来对待
 * 获取 Bean 的数量
 * 得到所有 Bean 的名字
 * 按照某个类型获取 Bean 列表等等。
 */
public interface ListableBeanFactory extends BeanFactory {

    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanNamesForType(Class<?> type);

    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

}