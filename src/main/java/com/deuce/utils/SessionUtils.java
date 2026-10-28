package com.deuce.utils;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {
    public static void fillSession(Session session, HttpSession hs){
        if (session != null && hs != null) {
            Enumeration<String> attributeNames = hs.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                Object value = hs.getAttribute(name);

                session.setAttribute(name, value);
            }
        }
    }

    public static boolean containsSession(ConcurrentHashMap<String, Session> sessionMap, String idSession){
        return sessionMap.containsKey(idSession);
    }

    public static void setSessionMap(ConcurrentHashMap<String, Session> sessionMap) {

    }

        public static void syncSession(Session session, HttpSession hs) {
            if (session != null && hs != null) {
                Map<String, Object> attributes = session.getAllAttributesMap();
                
                if (attributes != null) {
                    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                        hs.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
}
