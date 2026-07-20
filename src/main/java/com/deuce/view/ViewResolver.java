package com.deuce.view;

public class ViewResolver {
    private String prefix;
    private String viewName;
    private String extension;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getCheminCompletVue() {
        return prefix + viewName + extension;
    }
}