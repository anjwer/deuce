package com.deuce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.deuce.annotation.Auth;
import com.deuce.annotation.WebApi;
import com.deuce.utils.Mapping;
import com.deuce.utils.ParameterBinder;
import com.deuce.utils.Session;
import com.deuce.utils.SessionUtils;
import com.deuce.utils.UrlMethod;
import com.deuce.view.ModelAndView;
import com.deuce.view.ViewResolver;
import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

                


                // vérification de la session
                ConcurrentHashMap<String, Session> sessionMap = (ConcurrentHashMap<String, Session>) getServletContext().getAttribute("sessionMap");
                HttpSession hs = req.getSession();
                String idSession = req.getSession().getId();
                
                Session sessionUser = sessionMap.get(idSession);
                if (sessionUser == null) {
                    sessionUser = new Session();
                    sessionMap.put(idSession, sessionUser);
                }
                
                SessionUtils.fillSession(sessionUser, hs);
                // stocker la httpsession actuelle 
                req.setAttribute("currentSession", sessionUser);
                
                // vérifier, false = erreur 401
                if (!checkAuth(sessionUser, methode)) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise pour exécuter cette méthode");
                    return;
                } 

                // preparation des paramètres pour la methode
                Object[] parameters = resolveParameters(req, out, methode);

                out.println("<p> Arrivé ici </p>");
                // 

                for (int i = 0; i < parameters.length; i++) {
                    Object param = parameters[i];

                    out.println(
                        "<p> Paramètre " + i + " : "
                        + (param == null ? "NULL" : param.getClass().getName()) + "/<p>"
                    );
                    if (param != null) {
                        out.println(param);
                    }
                }


                // invokeee
                Object resultat = methode.invoke(instance, parameters);

                // synchronisation des sessions
                SessionUtils.syncSession(sessionUser, hs);
                renderResponse(req, res, out, mapping, resultat);

            } catch (Exception e) {
                out.println("<p>Erreur lors de l'invocation de la méthode : " + e.getMessage() + "</p>");
                Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) 
                      ? ((java.lang.reflect.InvocationTargetException) e).getTargetException() 
                      : e;
                
                // Affiche toute la vraie erreur avec la ligne exacte dans la console
                cause.printStackTrace(); 
                
                // Affichage temporaire à l'écran pour debug
                res.getWriter().write("Vraie erreur : " + cause.getClass().getName() + " - " + cause.getMessage());
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
    private Object[] resolveParameters(HttpServletRequest req, PrintWriter out, Method methode) {
        Class<?>[] parameterTypes = methode.getParameterTypes();
        Parameter[] parametersInfo = methode.getParameters();
        Object[] parameters = new Object[parameterTypes.length];

        // Map<String, String[]> parameterMap = req.getParameterMap();

        String injectableType = getServletContext().getInitParameter("injectableContextType");
        String injectableKey = getServletContext().getInitParameter("injectableContextKey");

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            String paramName = parametersInfo[i].getName();

            // ApplicationContext Spring
            if (injectableType != null && injectableKey != null && 
                parameterTypes[i].getName().equals(injectableType.trim())) {
                
                parameters[i] = getServletContext().getAttribute(injectableKey.trim());
            } else {
                // ignore casse
                /*String paramName = parametersInfo[i].getName();
                String rawValue = findParamIgnoreCase(req, paramName);

                parameters[i] = Utils.convertType(rawValue, parameterTypes[i]);*/
                parameters[i] = ParameterBinder.bind(req, paramType, paramName, out);
            }
        }
        return parameters;
    }

    /*private Object resolveObject(Class<?> targetType, String prefix, HttpServletRequest req) {
        // Cas 1 : Type simple (String, int, Integer, Boolean, etc.)
        if (Utils.isSimpleType(targetType)) {
            String rawValue = findParamIgnoreCase(req, prefix);
            return Utils.convertType(rawValue, targetType);
        }

        // Cas 2 : Gestion des Listes (ex: List<Client>)
        if (List.class.isAssignableFrom(targetType)) {
            // List<Object> list = new ArrayList<>();
            // // Note: Pour une liste, on récupère le type générique si besoin, 
            // // ou l'on peut itérer sur les indices prefix[0], prefix[1]...
            // int index = 0;
            // while (true) {
            //     String itemPrefix = prefix + "[" + index + "]";
            //     // On vérifie si un paramètre correspondant à cet index existe dans la requête
            //     if (!hasParamStartingWith(req, itemPrefix)) {
            //         break;
            //     }
            //     // Par défaut, si c'est une liste d'objets, on a besoin du type (souvent géré via un type générique)
            //     // Ici, faisons simple ou prévoyons le type de base de la liste si connu.
            //     index++;
            // }
            // return list.isEmpty() ? null : list;
        }

        // Cas 3 : Objet complexe (ex: Client) -> Utilisation des Setters
        try {
            // Instanciation de l'objet (prend le premier constructeur disponible, même avec des arguments si besoin)
            Object instance = Utils.createInstance(targetType);
            boolean populated = false;

            // On analyse toutes les méthodes de la classe à la recherche des setters
            for (Method method : targetType.getMethods()) {
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    Class<?> propertyType = method.getParameterTypes()[0];
                    
                    // Déduit le nom de la propriété à partir du setter (ex: setNom -> nom)
                    String propertyName = decapitalize(method.getName().substring(3));
                    String propertyPrefix = prefix + "." + propertyName;

                    // Résolution récursive de la valeur pour ce setter
                    Object value = resolveObject(propertyType, propertyPrefix, req);
                    
                    if (value != null) {
                        method.invoke(instance, value); // Appel du setter !
                        populated = true;
                    }
                }
            }
            return populated ? instance : null;
        } catch (Exception e) {
            return null;
        }
    }*/

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

    // true = peut excécuter la methode 
    // false = erreur 401
    private boolean checkAuth(Session sessionUser, Method methode){
        // verif si besoin d'auth
        if (methode.isAnnotationPresent(Auth.class)) {
            // vérifier si connecté 
            String authParam = getServletContext().getInitParameter("authParam");
            if (sessionUser.getAttribute(authParam)== null) {
                return false;
            }

            // vérifier les roles
            String roleParam = getServletContext().getInitParameter("roleParam");
            Auth annotation = methode.getAnnotation(Auth.class);
            String[] roles = annotation.roles();
            String rolesSession = (String) sessionUser.getAttribute(roleParam);

            return Arrays.asList(roles).contains(rolesSession);     
        }
        return true;

    }
}