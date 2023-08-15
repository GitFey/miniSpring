package com.minis.beans.factory.config;

import com.minis.beans.factory.ListableBeanFactory;

/**
 * 把 之前AutowireCapableBeanFactory、ListableBeanFactory 和 ConfigurableBeanFactory 合并在一起。
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

}