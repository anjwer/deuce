package com.deuce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.deuce.utils.Mapping;
import com.deuce.utils.UrlMethod;
import com.deuce.view.ModelAndView;
import com.deuce.view.ViewResolver;

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
            res.setContentType("text/html;charset=UTF-8");

            try (PrintWriter out = res.getWriter()) {
                out.println("<html><body>");
                // out.println("<p> hellooooo world </p>");

                String url = req.getRequestURI().substring(req.getContextPath().length());
                String method = req.getMethod();

                afficher(req, res, url, method, out);

                out.println("</body></html>");
            }
        }

    void afficher (HttpServletRequest req, HttpServletResponse res, String url, String method, PrintWriter out)
        throws ServletException, IOException {
            UrlMethod urlMethod = new UrlMethod(url, method);
            Mapping mapping = urlMap.get(urlMethod);

        if (urlMap == null) {
            out.println("<p>Erreur: urlMap est null. Le listener n'a pas bien initialisé les routes.</p>");
            return;
        }

        if (mapping != null) {
            out.println("<p>URL (cherchée) : " + urlMethod.getUrl() + " | methode : " + urlMethod.getMethod() + " | Mapping trouvé : Classe " + mapping.getController().getName() + " | Fonction: "
                        + mapping.getMethod().getName() + "</p>");

            try {
                Object instance = mapping.getController().getDeclaredConstructor().newInstance();
                Method methode = mapping.getMethod();
                
                Class<?>[] parameterTypes = methode.getParameterTypes();
                Object[] parametres = new Object[parameterTypes.length];
                
                String injectableType = getServletContext().getInitParameter("injectableContextType");
                String injectableKey = getServletContext().getInitParameter("injectableContextKey");
                
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (injectableType != null && injectableKey != null && 
                        parameterTypes[i].getName().equals(injectableType.trim())) {
                        parametres[i] = getServletContext().getAttribute(injectableKey.trim());
                    } else {
                        parametres[i] = null;
                    }
                }
                
                Object resultat = methode.invoke(instance, parametres);

                if (resultat instanceof ModelAndView) {
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
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
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
}