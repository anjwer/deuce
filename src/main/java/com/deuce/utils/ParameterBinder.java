package com.deuce.utils;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Map;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServletRequest;

public class ParameterBinder {  
    public static Object bind(HttpServletRequest req, Class<?> type, String paramName, PrintWriter out) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        
        if (isSimpleType(type)) {
            String rawValue = findParamIgnoreCase(parameterMap, paramName);

            if (rawValue == null || rawValue.trim().isEmpty()) {
                return null;
            }

            return Utils.convertType(rawValue, type);
        } else if (type.equals(com.deuce.utils.MapFile.class)) {
            try {
                boolean isMultipart = req.getContentType() != null && req.getContentType().toLowerCase().startsWith("multipart/");
                if (isMultipart) {
                    Part part = req.getPart(paramName);
                    if (part != null && part.getSize() > 0) {
                        return new com.deuce.utils.MapFile(part);
                    }
                }
            } catch (Exception e) {
                out.println("Erreur lors de la récupération du fichier pour " + paramName + " : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        try {
            // classe possède un constructeur par défaut 
            Object instance = type.getDeclaredConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                String lookupName = paramName + "." + field.getName();
                if (!hasParam(parameterMap, lookupName)) {
                    lookupName = field.getName();
                }

                Object fieldValue = bind(req, field.getType(), lookupName, out);
                if (fieldValue != null) {
                    field.set(instance, fieldValue);
                }
            }
            return instance;
        } catch (Exception e) {
            // TRÈS IMPORTANT : Affiche l'erreur exacte au lieu de renvoyer null en cachette
            
            out.println("Erreur de binding pour la classe " + type.getName() + " avec le paramètre " + paramName + " : " + e.getMessage());
            out.println("Classe : " + type.getName());
            out.println("Paramètre : " + paramName);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() || 
               type.equals(String.class) || 
               Number.class.isAssignableFrom(type) || 
               type.equals(Boolean.class) || 
               type.equals(Character.class);
    }

    private static boolean isMapFile(Class<?> type){
        return type.equals(com.deuce.utils.MapFile); 
    }
     
    private static String findParamIgnoreCase(Map<String, String[]> parameterMap, String name) {
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    return values[0];
                }
            }
        }
        return null;
    }

    private static boolean hasParam(Map<String, String[]> parameterMap, String name) {
        return findParamIgnoreCase(parameterMap, name) != null;
    }

    // private MapFile bindToFile(){

    // }
}
