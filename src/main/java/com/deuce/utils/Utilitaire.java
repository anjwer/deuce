package com.deuce.utils;

import java.io.File;
import java.lang.annotation.ElementType;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utilitaire {
    
    public static List<String> getAnnotatedList(String packageName, String annotation, ElementType type) throws Exception {
        List<String> annotatedList = new ArrayList<>();
        String path = packageName.replace('.', '/');

        // localisateur : donne le chemin complet pour trouver le dossier du package
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        
        if (resource != null) {
            // enlever les espace
            File directory = new File(resource.getFile().replace("%20", " "));

            if (directory.exists()) {
                File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
                if (files!=null) {
                    for (File file : files) {
                        String controller = getAnnotatedClass(file, packageName, annotation, type);
                        if (controller!=null){
                            annotatedList.add(controller);       
                        }
                    }

                }
            }
        }
        return annotatedList;
    }

    public static String getAnnotatedClass(File file, String packageName, String annotation, ElementType type){
        String fileName = file.getName();

        // nom complet avec package
        String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
        try{
            Class<?> clazz = Class.forName(className);
            
            // On détermine le nom de la classe (ex: "ChequeController")
            String simpleName = clazz.getSimpleName();

            Class<?> annot = Class.forName(annotation);
            if (annot!=null) {
                System.out.println("String ajoute pour le controller : " + simpleName);
                return simpleName;
            }
 
        }catch(ClassNotFoundException | NoClassDefFoundError e) {
            System.err.println("Impossible de charger la classe: " + className + " - " + e.getMessage());
        } 
        return null;
    }
}