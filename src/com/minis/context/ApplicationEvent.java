package com.minis.context;

import java.util.EventObject;

public class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    //构造器使用父类的。
    public ApplicationEvent(Object arg0) {
        super(arg0);
    }
}
