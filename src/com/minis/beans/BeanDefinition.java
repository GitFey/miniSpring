package com.minis.beans;


public class BeanDefinition {
    /**
     * 以下实例域为bean的最基本的属性
     */
    private String id;
    private String className;
    /**
     * 以下实例域为拓展bean时新增
     */
    String SCOPE_SINGLETON = "singleton"; //单例
    String SCOPE_PROTOTYPE = "prototype"; //原型
    private String scope = SCOPE_SINGLETON;
    private boolean lazyInit = false;  //表示 Bean 要不要在加载的时候初始化
    private String[] dependsOn; //记录 Bean 之间的依赖关系
    private String initMethodName;  //初始化方法的声明
    private volatile Object beanClass;
//    private ArgumentValues constructorArgumentValues; //构造器参数列表
//    private PropertyValues propertyValues; //构造器属性列表

    public BeanDefinition(String id,String className){
        this.id = id;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String getId() {
        return id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getInitMethodName() {
        return this.initMethodName;
    }
}

