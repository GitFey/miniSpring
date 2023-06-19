package com.minis.beans;

public class PropertyValue {
    /**
     * name属性：指定属性名
     * value属性 ： 指定属性值
     * isRef属性 ：是否是引用类型还是普通的值类型
     */
    private final String type;
    private final String name;
    private final Object value;
    private final boolean isRef;

    public PropertyValue(String type, String name, Object value, boolean isRef) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.isRef = isRef;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean getIsRef() {
        return isRef;
    }

}