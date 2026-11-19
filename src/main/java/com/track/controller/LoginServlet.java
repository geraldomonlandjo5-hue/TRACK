package com.track.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Démonstration uniquement : ne pas utiliser cette authentification en production.
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if ("admin@track.local".equals(email) && "TrackDemo2026!".equals(password)) {
            request.getSession(true).setAttribute("userEmail", email);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Identifiants de démonstration invalides.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
