package com.track.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Déconnexion par POST (protégé par CsrfFilter) : un simple lien ou une image ne peut plus déconnecter l'utilisateur. */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
