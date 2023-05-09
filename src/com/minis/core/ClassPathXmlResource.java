package com.minis.core;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.Iterator;
/**
 * 是外部配置信息的实现类，这里主要实现的是读取xml文件配置
 * 主要有三个实例域 ：
 *  1.document ：读取到的文件
 *  2.rootElement ：
 *  3.elementIterator ：
 * */
public class ClassPathXmlResource implements Resource{
    /**
     * 这里要做的就是解析xml
     * */
    Document document;
    Element rootElement;
    Iterator<Element> elementIterator;
    public ClassPathXmlResource(String fileName) {
        SAXReader saxReader = new SAXReader();
        URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
        //将配置文件装载进来，生成一个迭代器，可以用于遍历
        try {
            this.document = saxReader.read(xmlPath);
            this.rootElement = document.getRootElement();
            this.elementIterator = this.rootElement.elementIterator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasNext() {
        return this.elementIterator.hasNext();
    }
    public Object next() {
        return this.elementIterator.next();
    }
}