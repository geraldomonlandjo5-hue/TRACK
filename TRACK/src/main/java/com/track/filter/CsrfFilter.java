package com.track.filter;

import com.track.security.Csrf;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Vérifie le jeton CSRF sur tous les POST des zones protégées (colis, déconnexion). */
public class CsrfFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        if (session != null) Csrf.ensure(session, Csrf.APP_TOKEN);

        if ("POST".equalsIgnoreCase(request.getMethod())
                && !Csrf.valid(session, Csrf.APP_TOKEN, request.getParameter(Csrf.PARAM))) {
            if ("/logout".equals(request.getServletPath())) {      // session déjà expirée : on termine proprement
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Jeton de sécurité invalide.");
            }
            return;
        }
        chain.doFilter(req, res);
    }
}
