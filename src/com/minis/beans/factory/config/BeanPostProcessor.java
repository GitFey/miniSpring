package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;

public interface BeanPostProcessor {
    /**
     *  Bean 初始化之前调用。这个方法允许你对 Bean 进行修改或预处理操作，返回的对象将作为最终的 Bean 实例。
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在 Bean 初始化之后调用。这个方法允许你对 Bean 进行修改或后处理操作，返回的对象也将作为最终的 Bean 实例。
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */

    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
    void setBeanFactory(BeanFactory beanFactory);

}
