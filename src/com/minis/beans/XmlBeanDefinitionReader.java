package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.List;

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

            //handle properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                PVS.addPropertyValue(new PropertyValue(pType, pName, pValue, true));
            }
            beanDefinition.setPropertyValues(PVS);
            //end of handle properties

            //get constructor
            List<Element> constructorElements = element.elements("constructor-arg");
            ArgumentValues AVS = new ArgumentValues();
            for (Element e : constructorElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                AVS.addArgumentValue(new ArgumentValue(pType,pName,pValue));
            }
            beanDefinition.setConstructorArgumentValues(AVS);
            //end of handle constructor

            this.simpleBeanFactory.registerBeanDefinition(beanID,beanDefinition);
        }
    }


}