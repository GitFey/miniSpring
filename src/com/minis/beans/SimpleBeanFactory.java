package com.minis.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个简单的bean工厂实现类，同样有注册和get两个功能
 *主要有三个实例域 ：
 * 1.beanDefinitions，是一个泛型列表，存了所有的bean的定义
 * 2.beanNames，是一个字符串列表，存了所有bean的名字
 * 3.singletons，哈希表，key为bean的名字，value为bean实例
 * */
public class SimpleBeanFactory  extends DefaultSingletonBeanRegistry implements BeanFactory,BeanDefinitionRegistry{
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    //beanName和singletons在父级定义了，这里就删掉了

    public SimpleBeanFactory() {
    }

    /**getBean ： 容器的核心方法
     * 1.try to get bean
     * 2.   if no bean : (1)get beanDefinition (2)createBean  (3)registerBean
     * 3.return singleton bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object getBean(String beanName) throws BeansException {
        //先尝试直接拿bean实例
        Object singleton = this.getSingleton(beanName);
        //如果此时还没有这个bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            //获取bean的定义
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            singleton = createBean(beanDefinition);
            //新注册这个bean实例
            this.registerSingleton(beanName, singleton);
        }
        return singleton;
    }

    //接口新增方法，检查是否存在bean
    public Boolean containsBean(String name) {
        return containsSingleton(name);
    }


    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }


    public boolean isPrototype(String name) {
        return false;
    }


    public Class<?> getType(String name) {
        return null;
    }

    /**
     * 以下方法在拓展bean时新增
     * @param name
     * @param beanDefinition
     */
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
            }
        }
    }
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);

    }

    @Override
    public void removeBeanDefinition(String name) {

    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return false;
    }







    /**
     * creatBean ：
     * 创建一个obj，然后 ：
     *  1.doCreateBean() ： 根据beandefinition ，在ArgumentValues列表中拿到相应的类型和值，返回给obj
     *  2.handleProperties() ：  根据beandefinition ，在propertyValues列表中拿到相应的类型和值，在obj中增加
     * @param bd
     * @return
     */
    private Object createBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = doCreateBean(bd);

        this.earlySingletonObjects.put(bd.getId(), obj);

        try {
            clz = Class.forName(bd.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        handleProperties(bd, clz, obj);

        return obj;
    }

    /**
     * 处理constructor
     * 首先，获取 XML 配置中的属性值，这个时候它们都是通用的 Object 类型
     * 我们需要根据 type 字段的定义判断不同 Value 所属的类型，作为一个原始的实现这里我们只提供了 String、Integer 和 int 三种类型的判断。
     * 最终通过反射构造对象，将配置的属性值注入到了 Bean 对象中，实现构造器注入。
     * @param bd
     * @return
     */
    private Object doCreateBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;

        try {
            //class.forName : ask JVM find and load the class
            clz = Class.forName(bd.getClassName());

            //handle constructor : argumentValues store in ArgumentValues
            ArgumentValues argumentValues = bd.getConstructorArgumentValues();
            if (!argumentValues.isEmpty()) {
                // new 2 lists （of types and values in ArgumentValues） by those size
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues =   new Object[argumentValues.getArgumentCount()];
                //依次根据constructor中的类型和值来构造paramTypes与paramValues
                for (int i=0; i<argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
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
//                System.out.println(Arrays.toString(Arrays.stream(paramTypes).toArray()));
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
     * 处理property：
     * 和处理 constructor 相同，我们依然要通过 type 字段确定 Value 的归属类型。
     * 但不同之处在于，判断好归属类型后，我们还要手动构造 setter 方法，通过反射将属性值注入到 setter 方法之中。通过这种方式来实现对属性的赋值。
     * 其实代码的核心是通过 Java 的反射机制调用构造器及 setter 方法，在调用过程中根据具体的类型把属性值作为一个参数赋值进去。
     * 这也是所有的框架在实现 IoC 时的思路。反射技术是 IoC 容器赖以工作的基础。
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
                //如果属性不是引用类型，则根据属性的类型设置相应的参数类型，并将属性值赋给参数值数组。
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
                //如果属性是引用类型，则根据属性的类型动态加载相应的类，并通过调用 getBean 方法获取对应的依赖bean，并将其设置为参数值。
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
                //构造设置属性的方法名，方法名以 "set" 开头，后跟属性名称首字母大写的形式。
                String methodName = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);

                Method method = null;
                //通过反射获取类 clz 中与方法名和参数类型匹配的方法对象。
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                //通过反射调用获取到的方法对象，将参数值设置到目标对象 obj 的对应属性中。
                try {
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


    /**
     * 注册,遗留方法
     * */
//    public void registerBeanDefinition(BeanDefinition beanDefinition) {
//        this.beanDefinitions.put(beanDefinition.getId(), beanDefinition);
//    }
}
