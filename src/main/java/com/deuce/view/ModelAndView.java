package com.deuce.view;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String viewName;
    private Map<String, Object> attributes;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
        this.attributes = new HashMap<>();
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Object getAttribut(String nomAttribut) {
        if (this.attributes == null) {
            return null;
        }
        // if (nomAttribut == null || nomAttribut.isEmpty()) {
        //     throw new IllegalArgumentException("Le nom de l'attribut ne peut pas être null ou vide.");
        // }
        if (!this.attributes.containsKey(nomAttribut)) {
            throw new IllegalArgumentException("L'attribut " + nomAttribut + " n'existe pas.");
        }
        return this.attributes.get(nomAttribut);
    }

    public void addAttribut(String nomAttribut, Object valeur) {
        
        // if (nomAttribut == null || nomAttribut.isEmpty()) {
        //     throw new IllegalArgumentException("Le nom de l'attribut ne peut pas être null ou vide.");
        // }
        if (this.attributes.containsKey(nomAttribut)) {
            throw new IllegalArgumentException("L'attribut " + nomAttribut + " existe déjà.");
        }
        this.attributes.put(nomAttribut, valeur);
    }
}