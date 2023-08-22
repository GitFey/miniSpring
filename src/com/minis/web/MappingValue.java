package com.minis.web;

/**
 * 实体类 MappingValue 里的三个属性：uri、clz 与 method，
 * 分别与 minisMVC-servlet.xml 中标签的属性 id、class 与 value 对应。
 */
public class MappingValue {
    String uri;
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    String clz;
    public String getClz() {
        return clz;
    }
    public void setClz(String clz) {
        this.clz = clz;
    }
    String method;
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public MappingValue(String uri, String clz, String method) {
        this.uri = uri;
        this.clz = clz;
        this.method = method;
    }
}