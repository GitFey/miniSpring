package com.minis.beans.factory.annotation;

import java.lang.reflect.Field;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.AutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;

/**
 * @Autowired注解的实现类
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private BeanFactory beanFactory;


    /**
     * 先通过反射获取bean的类和其中的属性
     * 然后判断类里面的每一个属性是不是带有 Autowired 注解
     * 如果有，就根据属性名获取 Bean。
     * 有了 Bean 之后，通过反射设置属性值，完成依赖注入。
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;

        Class<?> clazz = result.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if(fields!=null){
            for(Field field : fields){
                boolean isAutowired = field.isAnnotationPresent(Autowired.class);
                //如果带有@Autowired注解
                if(isAutowired){
                    String fieldName = field.getName();
                    //通过getBean() ，从bean工厂中获取对应的依赖对象
                    Object autowiredObj = this.getBeanFactory().getBean(fieldName);
                    try {
                        //将字段设置为可访问，以便能够修改私有字段。
                        field.setAccessible(true);
                        //将依赖对象注入到字段中。
                        field.set(bean, autowiredObj);
                        //打印日志，表示成功进行了自动注入。
                        System.out.println("autowire " + fieldName + " for bean " + beanName);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}