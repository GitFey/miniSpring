package com.minis.beans;


public class ArgumentValue {
    private Object value;
    private String type;
    private String name;
    public ArgumentValue(Object value, String type) {
        this.value = value;
        this.type = type;
    }
    public ArgumentValue(Object value, String type, String name) {
        this.value = value;
        this.type = type;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public Object getValue(){
        return value;
    }

    public String getType() {
        return type;
    }
}
