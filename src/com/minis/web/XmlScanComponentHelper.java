package com.minis.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 用于处理minisMVC-servlet.xml结构
 *
 */
public class XmlScanComponentHelper {
    public static List<String> getNodeValue(URL xmlPath) {
        //把扫描到的 package 存储在packages 这个结构里。
        List<String> packages = new ArrayList<>();
        SAXReader saxReader=new SAXReader();
        Document document = null;
        try {
            //加载配置文件
            document = saxReader.read(xmlPath);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        Iterator it = root.elementIterator();

        while (it.hasNext()) {
            //得到XML中所有的base-package节点
            Element element = (Element) it.next();
            packages.add(element.attributeValue("base-package"));
        }

        return packages;
    }

}