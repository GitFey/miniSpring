package com.minis.context;

import java.util.EventObject;

/**
 * ApplicationEvent 类继承了 EventObject
 * EventObject是 Java 标准库中的一个基类。继承 EventObject 可以让你使用事件对象的基本功能，比如获得事件源对象等。
 */

public class ApplicationEvent extends EventObject {
    /**
     * serialVersionUID 是一个用于序列化的版本号，它在进行序列化和反序列化时用于检查类的版本一致性。
     */
    private static final long serialVersionUID = 1L;
    protected String msg = null;

    /**
     * 构造函数 public ApplicationEvent(Object arg0) 接收一个参数，这个参数通常表示事件的源对象。
     * 在构造函数中，你将这个源对象传递给父类的构造函数，同时设置 msg 字段来存储传入的参数的字符串表示。
     * @param arg0
     */
    public ApplicationEvent(Object arg0) {
        super(arg0);
        this.msg = arg0.toString();
    }


}
