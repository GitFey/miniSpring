package com.minis.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.support.AbstractBeanFactory;


/**
 * AutowireCapableBeanFactory 是一个扩展了 AbstractBeanFactory 的类，提供了自动装配的功能。
 * 它管理了一个 AutowiredAnnotationBeanPostProcessor 实例的列表，用于在初始化 bean 时对其进行后处理。
 */
public abstract class AbstractAutowireCapableBeanFactory
        extends AbstractBeanFactory  implements AutowireCapableBeanFactory{

    // 存储 AutowiredAnnotationBeanPostProcessor 实例的列表
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    /**
     * 将 AutowiredAnnotationBeanPostProcessor 实例添加到后处理器列表中。
     * @param beanPostProcessor 要添加的 AutowiredAnnotationBeanPostProcessor 实例。
     */
//    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
//        // 确保唯一性，先移除已存在的实例再添加新的实例
//        this.beanPostProcessors.remove(beanPostProcessor);
//        this.beanPostProcessors.add(beanPostProcessor);
//    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }
    /**
     * 获取后处理器列表中 AutowiredAnnotationBeanPostProcessor 实例的数量。
     * @return 后处理器列表中 AutowiredAnnotationBeanPostProcessor 实例的数量。
     */
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    /**
     * 获取后处理器列表中 AutowiredAnnotationBeanPostProcessor 实例的列表。
     * @return 后处理器列表中 AutowiredAnnotationBeanPostProcessor 实例的列表。
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    /**
     * 在 bean 初始化之前应用 bean 后处理器。
     * 调用所有 AutowiredAnnotationBeanPostProcessor 实例的 postProcessBeforeInitialization 方法。
     * @param existingBean 初始化之前的 bean 实例。
     * @param beanName bean 的名称。
     * @return 应用后处理器后的 bean 实例。
     * @throws BeansException 如果在后处理期间发生错误。
     */
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            // 设置当前 AutowiredAnnotationBeanPostProcessor 的 bean 工厂
            beanProcessor.setBeanFactory(this);
            // 调用 bean 处理器的 postProcessBeforeInitialization 方法
            result = beanProcessor.postProcessBeforeInitialization(result, beanName);
            // 如果处理结果为 null，立即返回
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    /**
     * 在 bean 初始化之后应用 bean 后处理器。
     * 调用所有 BeanPostProcessor 实例的 postProcessAfterInitialization 方法。
     * @param existingBean 初始化之后的 bean 实例。
     * @param beanName bean 的名称。
     * @return 应用后处理器后的 bean 实例。
     * @throws BeansException 如果在后处理期间发生错误。
     */
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            // 调用 bean 处理器的 postProcessAfterInitialization 方法
            result = beanProcessor.postProcessAfterInitialization(result, beanName);
            // 如果处理结果为 null，立即返回
            if (result == null) {
                return result;
            }
        }
        return result;
    }

}
