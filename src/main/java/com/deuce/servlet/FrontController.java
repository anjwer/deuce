package com.deuce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

                afficher(req, res, url, out);
                // if (controllersList != null) out.println("null");

                out.println("</body></html>");
            }
        }

    void afficher (HttpServletRequest req, HttpServletResponse res, String url, PrintWriter out)
        throws ServletException, IOException {
        if (urlMap.containsKey(url)){
            out.println("trouve");
            out.println("<p>" + url + "\n" + urlMap.get(url) + "</p>");
            
        } else {
            out.println("non trouve");
            for (Map.Entry<String, Mapping> entry : urlMap.entrySet()) {
                out.println("<p>" + entry.getKey() + "\n" + entry.getValue() + "</p>");
            }
        }
    }
}