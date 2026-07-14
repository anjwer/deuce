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

    public Object getAttribut(String nom_attribut) {
        if (this.attributes == null) {
            return null;
        }
        if (nom_attribut == null || nom_attribut.isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'attribut ne peut pas être null ou vide.");
        }
        if (!this.attributes.containsKey(nom_attribut)) {
            throw new IllegalArgumentException("L'attribut " + nom_attribut + " n'existe pas.");
        }
        return this.attributes.get(nom_attribut);
    }

    public void addAttribut(String nom_attribut, Object valeur) {
        if (nom_attribut == null || nom_attribut.isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'attribut ne peut pas être null ou vide.");
        }
        if (this.attributes.containsKey(nom_attribut)) {
            throw new IllegalArgumentException("L'attribut " + nom_attribut + " existe déjà.");
        }
        this.attributes.put(nom_attribut, valeur);
    }
}