package com.track.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

// "" = uniquement la racine du site (/track/). Avec "/" ce servlet remplaçait le servlet
// par défaut et interceptait aussi /assets/css/app.css et /assets/js/app.js.
@WebServlet(urlPatterns = {""})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
