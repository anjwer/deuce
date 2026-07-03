package com.deuce.utils;

import java.lang.reflect.Method;

public class Mapping {
    private Class<?> controller;
    private Method method;

    public Class<?> getController() {
        return controller;
    }

    public void setController(Class<?> controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Mapping \n" +
                "controller=" + (controller != null ? controller.getName() : "null") +
                ", method=" + (method != null ? method.getName() : "null") +
                '\n';
    }
}