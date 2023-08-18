package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 此类实现了循环依赖问题，体现在getBean(), createBean() 和 doCreateBean() 中。
 * createBean() 方法中调用了一个 doCreateBean(bd) 方法，专门负责创建早期的毛胚实例。
 * 毛胚实例创建好后会放在 earlySingletonObjects 结构中，然后 createBean() 方法再调用 handleProperties() 补齐这些 property 的值。
 * 在 getBean() 方法中，首先要判断有没有已经创建好的 bean，
 * -有的话直接取出来，如果没有就检查 earlySingletonObjects 中有没有相应的毛胚 Bean，
 * -有的话直接取出来，没有的话就去创建，
 * -并且会根据 Bean 之间的依赖关系把相关的 Bean 全部创建好。
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory,BeanDefinitionRegistry{
    protected Map<String,BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<>(256);
    protected List<String> beanDefinitionNames=new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    public AbstractBeanFactory() {
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getBean(String beanName) throws BeansException{
        //先尝试直接从容器中获取bean实例
        Object singleton = this.getSingleton(beanName);

        if (singleton == null) {
            //如果没有实例，则尝试从毛胚实例中获取
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                //如果连毛胚都没有，则创建bean实例并注册
                System.out.println("get bean null -------------- " + beanName);
                BeanDefinition bd = beanDefinitionMap.get(beanName);
                singleton=createBean(bd);
                this.registerBean(beanName, singleton);

                //beanpostprocessor
                //step 1 : postProcessBeforeInitialization
                applyBeanPostProcessorsBeforeInitialization(singleton, beanName);

                //step 2 : init-method
                if (bd.getInitMethodName() != null && !bd.getInitMethodName().equals("")) {
                    invokeInitMethod(bd, singleton);
                }

                //step 3 : postProcessAfterInitialization
                applyBeanPostProcessorsAfterInitialization(singleton, beanName);
            }

        }
        if (singleton == null) {
            throw new BeansException("bean is null.");
        }
        return singleton;
    }

    private void invokeInitMethod(BeanDefinition bd, Object obj) {
        Class<?> clz = obj.getClass();
        Method method = null;
        try {
            method = clz.getMethod(bd.getInitMethodName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean containsBean(String name) {
        return containsSingleton(name);
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);

    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        this.beanDefinitionMap.put(name,bd);
        this.beanDefinitionNames.add(name);
        if (!bd.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);

    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    private Object createBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = doCreateBean(bd);

        this.earlySingletonObjects.put(bd.getId(), obj);

        try {
            clz = Class.forName(bd.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        populateBean(bd, clz, obj);

        return obj;
    }

    private Object doCreateBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;

        try {
            clz = Class.forName(bd.getClassName());

            //handle constructor
            ConstructorArgumentValues argumentValues = bd.getConstructorArgumentValues();
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues =   new Object[argumentValues.getArgumentCount()];
                for (int i=0; i<argumentValues.getArgumentCount(); i++) {
                    ConstructorArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                    else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    }
                    else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue()).intValue();
                    }
                    else {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                try {
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                obj = clz.newInstance();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(bd.getId() + " bean created. " + bd.getClassName() + " : " + obj.toString());

        return obj;

    }

    /**
     * 给bean输入数据（注入依赖）
     * @param bd
     * @param clz
     * @param obj
     */
    private void populateBean(BeanDefinition bd, Class<?> clz, Object obj) {
        handleProperties(bd, clz, obj);
    }


    /**
     * 在完成依赖注入（Dependency Injection）的过程
     * 将属性值设置到对象中，以构建完整的 Bean 对象。
     * 如果属性不是引用，根据属性的类型设置参数类型 paramTypes，然后将值 pValue 设置到参数值数组 paramValues 中。
     * 如果属性是引用，对 ref 所指向的另一个 Bean 再次调用 getBean() 方法，这个方法会获取到另一个 Bean 实例，这样就实现了另一个 Bean 的注入。
     * 通过拼接属性名生成相应的 setter 方法名，然后使用反射获取这个方法，并通过反射调用该方法将属性值设置到目标对象 obj 中。
     * @param bd
     * @param clz
     * @param obj
     */
    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        //handle properties
        System.out.println("handle properties for bean : " + bd.getId());
        PropertyValues propertyValues = bd.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (int i=0; i<propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.getIsRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues =   new Object[1];
                //非引用的情况
                if (!isRef) {
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    }
                    else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    }
                    else if ("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    }
                    else {
                        paramTypes[0] = String.class;
                    }

                    paramValues[0] = pValue;
                }
                //是引用的情况
                else { //is ref, create the dependent beans
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        paramValues[0] = getBean((String)pValue);
                    } catch (BeansException e) {
                        e.printStackTrace();
                    }
                }
                //通过拼接属性名生成相应的 setter 方法名
                String methodName = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);

                Method method = null;
                try {
                    //通过反射获取 setter 方法
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                try {
                    //执行方法，实现依赖注入
                    method.invoke(obj, paramValues);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    abstract public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    abstract public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

}