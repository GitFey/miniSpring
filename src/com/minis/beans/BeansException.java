package com.minis.beans;

public class BeansException extends Exception {
    public BeansException(String msg) {
        //直接调用父类（Exception）处理并抛出异常
        super(msg);
    }

    public BeansException() {

    }
}