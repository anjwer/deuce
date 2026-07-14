package com.deuce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.deuce.annotation.Controller;
import com.deuce.utils.Mapping;
import com.deuce.utils.UrlMethod;
import com.deuce.utils.Utilitaire;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private Map<UrlMethod, Mapping> urlMap;
    
    public void init() throws ServletException {
        urlMap = new HashMap<>();
        try {
            String packageName = getServletConfig().getInitParameter("packageControllers");
            Utilitaire.setUrlMap(urlMap, packageName, Controller.class);
        } catch (Exception e) {
            System.out.println("Erreur lors de la recuperation des controllers : " + e.getMessage());
        } 
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
            
            processRequest(req, res);
        }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
            processRequest(req, res);
        }    

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
            res.setContentType("text/html;charset=UTF-8");

            try (PrintWriter out = res.getWriter()) {
                out.println("<html><body>");
                out.println("<p> hellooooo world </p>");

                String url = req.getRequestURI().substring(req.getContextPath().length());
                String method = req.getMethod();

                afficher(req, res, url, method, out);
                // if (controllersList != null) out.println("null");

                out.println("</body></html>");
            }
        }

    void afficher (HttpServletRequest req, HttpServletResponse res, String url, String method, PrintWriter out)
        throws ServletException, IOException {
            UrlMethod urlMethod = new UrlMethod(url, method);
            Mapping mapping = urlMap.get(urlMethod);

        if (urlMap != null){
            out.println("<p>URL: " + urlMethod.getUrl() + " avec la methode : " + urlMethod.getMethod() + "| Classe: " + mapping.getClass().getName() + " | Fonction: "
                        + mapping.getMethod().getName() + "</p>");

            try {
                Object instance = mapping.getClass().getDeclaredConstructor().newInstance();
                Method methode = mapping.getMethod();
                Object resultat = methode.invoke(instance);

                out.print("<script>console.log('" + resultat.toString() + "');</script>");
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                out.println("<p>Erreur lors de l'invocation de la méthode : " + e.getMessage() + "</p>");
            }
            
        } else {
            out.println("non trouve");
            for (UrlMethod urlMethodDisponible : urlMap.keySet()) {
                Mapping mappingDisponible = urlMap.get(urlMethodDisponible);
                out.println("<p>URL: " + urlMethodDisponible.getUrl() + " avec la methode : " + urlMethodDisponible.getMethod() + "| Classe: " + mappingDisponible.getClasse().getName() + " | Fonction: "
                        + mappingDisponible.getMethod().getName() + "</p>");
            }
        }
    }
}