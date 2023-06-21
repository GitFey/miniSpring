package com.minis.context;

import com.minis.beans.*;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.support.SimpleBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

/**
 * ClassPathXmlApplicationContext是根据name获取bean，实现了beanFactory接口
 * 实例域为BeanFactory
 * 工作流程为 ： 先解析XML文件中的内容，再进行实例化及加载
 * */
public class ClassPathXmlApplicationContext implements BeanFactory,ApplicationEventPublisher{
    SimpleBeanFactory beanFactory;
    //context负责整合容器的启动过程，读外部配置，解析Bean定义，创建BeanFactory
    public ClassPathXmlApplicationContext(String fileName){
        this(fileName, true);
    }
    public ClassPathXmlApplicationContext(String fileName,boolean isRefresh) {
        //1. 解析 XML 文件中的内容。
        Resource resource = new ClassPathXmlResource(fileName);
        //2. 先定义一个BeanFactory变量
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        //3.XmlBeanDefinitionReader会将XML 文件中的内容转化成我们需要的BeanDefinition并注册
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        //搞完咯
        this.beanFactory = beanFactory;
        if (isRefresh) {
            this.beanFactory.refresh();
        }
    }
    //context再对外提供一个getBean，底下就是调用的BeanFactory对应的方法
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }


    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }


    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
    }

    @Override
    public boolean isSingleton(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}