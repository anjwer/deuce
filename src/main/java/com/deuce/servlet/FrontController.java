package com.deuce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import com.deuce.annotation.WebApi;
import com.deuce.utils.Mapping;
import com.deuce.utils.UrlMethod;
import com.deuce.view.ModelAndView;
import com.deuce.view.ViewResolver;
import com.google.gson.Gson;

import com.deuce.utils.Utils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private Map<UrlMethod, Mapping> urlMap;
    
    public void init() throws ServletException {
        urlMap = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("mapping");
        if (urlMap == null) {
            System.out.println("Attention: urlMap n'a pas pu être récupéré depuis le ServletContext.");
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
            try (PrintWriter out = res.getWriter()) {
                String url = req.getRequestURI().substring(req.getContextPath().length());
                String method = req.getMethod();
                
                afficher(req, res, url, method, out);  
            }
        }

    void afficher(HttpServletRequest req, HttpServletResponse res, String url, String method, PrintWriter out)
        throws ServletException, IOException {
        UrlMethod urlMethod = new UrlMethod(url, method);
        Mapping mapping = urlMap.get(urlMethod);

        if (urlMap == null) {
            out.println("<p>Erreur: urlMap est null. Le listener n'a pas bien initialisé les routes.</p>");
            return;
        }

        if (mapping != null) {
            try {
                // creation d'une instance du controller qu'on recup depuis le mapping
                Object instance = mapping.getController().getDeclaredConstructor().newInstance();
                
                // récup la méthode a executer
                Method methode = mapping.getMethod();
                // preparation des paramètres pour la methode
                Object[] parameters = resolveParameters(req, methode);

                // invokeee
                Object resultat = methode.invoke(instance, parameters);
                renderResponse(req, res, out, mapping, resultat);

            } catch (Exception e) {
                out.println("<p>Erreur lors de l'invocation de la méthode : " + e.getMessage() + "</p>");
            }
        } else {
            out.println("<p>Route non trouvée pour l'URL : " + urlMethod.getUrl() + "</p>");
            out.println("<p>Routes disponibles :</p><ul>");
            for (UrlMethod urlMethodDisponible : urlMap.keySet()) {
                Mapping mappingDisponible = urlMap.get(urlMethodDisponible);
                out.println("<li>URL: " + urlMethodDisponible.getUrl() + " (" + urlMethodDisponible.getMethod() + ") -> Classe: " + mappingDisponible.getController().getName() + " | Fonction: "
                        + mappingDisponible.getMethod().getName() + "</li>");
            }
            out.println("</ul>");
        }
    }


    // préparation et conversion des paramètres pour la méthode à invoke + initialisation spring
    private Object[] resolveParameters(HttpServletRequest req, Method methode) {
        Class<?>[] parameterTypes = methode.getParameterTypes();
        Parameter[] parametersInfo = methode.getParameters();
        Object[] parameters = new Object[parameterTypes.length];

        String injectableType = getServletContext().getInitParameter("injectableContextType");
        String injectableKey = getServletContext().getInitParameter("injectableContextKey");

        for (int i = 0; i < parameterTypes.length; i++) {
            // ApplicationContext Spring
            if (injectableType != null && injectableKey != null && 
                parameterTypes[i].getName().equals(injectableType.trim())) {
                
                parameters[i] = getServletContext().getAttribute(injectableKey.trim());
            } else {
                // ignore casse
                String paramName = parametersInfo[i].getName();
                String rawValue = findParamIgnoreCase(req, paramName);

                parameters[i] = Utils.convertType(rawValue, parameterTypes[i]);
            }
        }
        return parameters;
    }

    private String findParamIgnoreCase(HttpServletRequest req, String name) {
        if (req.getParameter(name) != null) return req.getParameter(name);
        
        // sans tenir compte de la casse
        for (String key : req.getParameterMap().keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return req.getParameter(key);
            }
        }
        return null;
    }

    private void renderResponse(HttpServletRequest req, HttpServletResponse res, PrintWriter out, Mapping mapping, Object resultat) 
            throws ServletException, IOException {
        
        if (mapping.getMethod().isAnnotationPresent(WebApi.class)) {
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");

            if (resultat instanceof String) {
                out.println(resultat);
                return;
            }
            Gson gson = new Gson();
            out.print(gson.toJson(resultat));
            out.flush();
        } else if (resultat instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) resultat;
            ViewResolver viewResolver = new ViewResolver();
            viewResolver.setViewName(mv.getViewName());
            viewResolver.setPrefix(getServletContext().getInitParameter("prefixView"));
            viewResolver.setExtension(getServletContext().getInitParameter("extensionView"));

            for (Map.Entry<String, Object> entry : mv.getAttributes().entrySet()) {
                req.setAttribute(entry.getKey(), entry.getValue());
            }

            RequestDispatcher dispatcher = req.getRequestDispatcher(viewResolver.getCheminCompletVue());
            dispatcher.forward(req, res);
        } else {
            out.println("<p>Le résultat de la méthode n'est pas de type ModelAndView.</p>");
        }
    }
}