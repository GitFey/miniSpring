package com.minis.beans;

public interface BeanFactory {
    /**
     * bean工厂接口，需要实现的是两个功能 ：
     * 1.注册bean
     * 2.根据id获取bean
     * */
    Object getBean(String beanName) throws BeansException;
    Boolean containsBean(String name);

    /**
     * registerBean方法在拓展beanfactory接口时取消
     * @param beanName
     * @param obj
     */
    void registerBean(String beanName, Object obj);

    /**
     * 以下内容为拓展beanfactory接口时新增
     * @param name
     * @return
     */
    boolean isSingleton(String name); //新增 Singleton 的判断
    boolean isPrototype(String name); //新增 Prototype 的判断
    Class<?> getType(String name); //获取 Bean 的类型
}