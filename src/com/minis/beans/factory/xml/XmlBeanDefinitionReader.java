package com.minis.beans.factory.xml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.core.Resource;

/**
 * 用于从 XML 文件中读取和解析 Bean 的定义，并将它们注册到 AbstractBeanFactory 中。
 */

public class XmlBeanDefinitionReader {
    AbstractBeanFactory bf;
    public XmlBeanDefinitionReader(AbstractBeanFactory bf) {
        this.bf = bf;
    }

    /**
     * 用于加载和解析 XML 文件中的 Bean 定义。
     * 它迭代处理 XML 中的每个 <bean> 元素，提取各种属性，构建 BeanDefinition 对象，并将其注册到 AbstractBeanFactory 中。
     * @param res
     */
    public void loadBeanDefinitions(Resource res) {
        while (res.hasNext()) {
            Element element = (Element)res.next();
            String beanID=element.attributeValue("id");
            String beanClassName=element.attributeValue("class");
            String initMethodName=element.attributeValue("init-method");

            BeanDefinition beanDefinition=new BeanDefinition(beanID,beanClassName);
            /**
             * 在处理 <constructor-arg> 元素时，
             * 解析了构造函数的参数类型、名称和值，然后创建了 ConstructorArgumentValue 对象，
             * 并将其添加到 ConstructorArgumentValues 中，
             * 最终设置给 BeanDefinition。
             */
            //get constructor
            List<Element> constructorElements = element.elements("constructor-arg");
            ConstructorArgumentValues AVS = new ConstructorArgumentValues();
            for (Element e : constructorElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                AVS.addArgumentValue(new ConstructorArgumentValue(pType,pName,pValue));
            }
            beanDefinition.setConstructorArgumentValues(AVS);
            //end of handle constructor
            /**
             * 在处理 <property> 元素时，
             * 解析了属性的类型、名称、值和引用（如果有），
             * 然后创建了 PropertyValue 对象，
             * 并将其添加到 PropertyValues 中，
             * 最终设置给 BeanDefinition。
             */
            //handle properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                //引用
                String pRef = e.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                } else if (pRef != null && !pRef.equals("")) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(PVS);
            //将集合转换为数组的语法。传递一个新的空字符串数组作为参数，用于指示要创建的数组类型。Java 在内部会根据集合的大小自动创建一个适当大小的数组，并将集合中的元素复制到数组中。
            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);
            //end of handle properties

            beanDefinition.setInitMethodName(initMethodName);

            this.bf.registerBeanDefinition(beanID,beanDefinition);
        }
    }



}