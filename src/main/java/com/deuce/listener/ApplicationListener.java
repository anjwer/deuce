package com.deuce.listener;

import java.util.HashMap;
import java.util.Map;

import com.deuce.annotation.Controller;
import com.deuce.utils.Mapping;
import com.deuce.utils.UrlMethod;
import com.deuce.utils.Utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ApplicationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String packageController = context.getInitParameter("packageControllers");
        if (packageController == null) {
            packageController = ""; 
        }

        Map<UrlMethod, Mapping> mapping = new HashMap<>();
        
        try {
            Utils.setUrlMap(mapping, packageController, Controller.class);
            context.setAttribute("mapping", mapping);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation du mapping (Listener) : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Code à exécuter lors de l'arrêt de l'application (optionnel)
    }
}
