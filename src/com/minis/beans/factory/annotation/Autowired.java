package com.minis.beans.factory.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 命名了一个叫做Autowired的注解
 */
//指定了该注解的应用目标。在这种情况下，@Autowired 注解应用于类的字段（Field）。它表示被标记的字段需要进行自动装配。
@Target(ElementType.FIELD)
//指定了该注解的保留策略。在这种情况下，@Autowired 注解在运行时仍然可用。它表示该注解的信息将在运行时通过反射机制被保留和使用。
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
