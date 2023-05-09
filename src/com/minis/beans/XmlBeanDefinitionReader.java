package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;
/**
 * 这里是将在ClassPathXmlResource中解析好的xml转换成bean
 * */
public class XmlBeanDefinitionReader {
    //原来实例域是BeanFactory，现在改成SimpleBeanFactory
    SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
            this.simpleBeanFactory.registerBeanDefinition(beanDefinition);
        }
    }


}