package com.deuce.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import com.deuce.annotation.UrlMapping;

public class Utils {
    
    public static File[] getDirectoryFiles(String packageName){
        File [] files = new File[0];
        String path = packageName.replace('.', '/');

        // localisateur : donne le chemin complet pour trouver le dossier du package
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        
        if (resource != null) {
            // enlever les espace
            File directory = new File(resource.getFile().replace("%20", " "));

            if (directory.exists()) {
                files = directory.listFiles((dir, name) -> name.endsWith(".class"));
            }
        }
        return files;
    }


   /* public static Mapping getMappingController(String packageName, Class<? extends Annotation> controllerAnnotation,
        Class<? extends Annotation> methodAnnotation) {

        Mapping mapping = new Mapping();
        File[] files = getDirectoryFiles(packageName);

        if (files != null) {
            for (File file : files) {
                String className = packageName + "." + file.getName().replace(".class", "");

                try {
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(controllerAnnotation)) {
                        for (Method m : clazz.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(methodAnnotation)) {
                                mapping.setController(clazz);
                                mapping.setMethod(m);
                                return mapping;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }*/

    public static void setUrlMap(Map<UrlMethod, Mapping> urlMap, String packageName, Class<? extends Annotation> controllerAnnotation){
        File[] files = getDirectoryFiles(packageName);

        if (files != null) {
            for (File file : files) {
                String className = packageName + "." + file.getName().replace(".class", "");

                try {
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(controllerAnnotation)) {
                        for (Method m : clazz.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(UrlMapping.class)) {
                                Mapping mapping = new Mapping();
                                mapping.setController(clazz);
                                mapping.setMethod(m);

                                UrlMapping annotation = m.getAnnotation(UrlMapping.class);

                                UrlMethod urlMethod = new UrlMethod();
                                urlMethod.setUrl(annotation.value());
                                urlMethod.setMethod(annotation.method());

                                urlMap.put(urlMethod, mapping);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
 
    }

    public static Object convertType(String input, Class<?> targetType){
        if (targetType == String.class) {
            return input;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(input);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(input);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(input);
        }
        
        return input;
    }

}