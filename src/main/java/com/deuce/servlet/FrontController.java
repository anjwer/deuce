package com.deuce.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.util.List;

import com.deuce.utils.Utilitaire;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private List<String> listeControllers;
    
    public void init() throws ServletException {
        try {
            String packageName = getServletConfig().getInitParameter("packageControllers");
            listeControllers = Utilitaire.getAnnotatedList(packageName, "com.deuce.annotation.Controller",  ElementType.TYPE);
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
                // if (listeControllers != null) out.println("null");
                for (String controller : listeControllers) {
                    out.println("<p>" + controller + "</p>");
                    
                }
                out.println("</body></html>");
            }
        }
}