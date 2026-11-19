package com.track.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/shipments/*"})
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (request.getSession(false) == null
                || request.getSession(false).getAttribute("userEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }
}
