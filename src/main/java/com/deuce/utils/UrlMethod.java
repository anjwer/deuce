package com.deuce.utils;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private String method;

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getMethod(){
        return method;
    }

    public void setMethod(String method){
        this.method = method;
    }

    @Override
    public boolean equals(UrlMethod urlMethod){
        if (this == urlMethod) return true;
        return url.equals(urlMethod.getUrl()) && method.equalsIgnoreCase(urlMethod.getMethod());
    }

     @Override
    public int hashCode(){
        return  Objects.hash(url, method);
    }
}