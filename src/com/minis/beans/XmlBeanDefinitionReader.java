package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;
/**
 * 这里是将在ClassPathXmlResource中解析好的xml转换成bean
 * */
public class XmlBeanDefinitionReader {
    BeanFactory bf;
    public XmlBeanDefinitionReader(BeanFactory bf) {
        this.bf = bf;
    }
    public void loadBeanDefinitions(Resource res) {
        while (res.hasNext()) {
            Element element = (Element)res.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID,beanClassName);
            //注册
            this.bf.registerBeanDefinition(beanDefinition);
        }

    }



}