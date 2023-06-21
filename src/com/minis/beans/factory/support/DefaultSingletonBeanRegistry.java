package com.minis.beans.factory.support;


import com.minis.beans.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    //容器中存放所有bean的名称的列表
    protected List<String> beanNames = new ArrayList<>();
    //容器中存放所有bean实例的map，ConcurrentHashMap线程安全
    protected Map<String, Object> singletons = new ConcurrentHashMap<>(256);
    public void registerSingleton(String beanName, Object singletonObject) {
        //关键字 synchronized,保证多线程下正常
        synchronized (this.singletons) {
            Object oldObject = this.singletons.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }

            this.singletons.put(beanName, singletonObject);
            this.beanNames.add(beanName);
            System.out.println(" bean registerded............. " + beanName);
        }
    }
    public Object getSingleton(String beanName) {
        return this.singletons.get(beanName);
    }
    public boolean containsSingleton(String beanName) {
        return this.singletons.containsKey(beanName);
    }
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }
    protected void removeSingleton(String beanName) {
        synchronized (this.singletons) {
            this.beanNames.remove(beanName);
            this.singletons.remove(beanName);
        }
    }
}