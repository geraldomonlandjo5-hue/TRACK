package com.track.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/** En-têtes de sécurité HTTP sur toutes les réponses. */
public class SecurityHeadersFilter implements Filter {
    // Bootstrap et Google Fonts sont chargés depuis un CDN : à héberger localement pour retirer ces origines.
    private static final String CSP = "default-src 'self'; "
            + "script-src 'self' https://cdn.jsdelivr.net; "
            + "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; "
            + "font-src 'self' https://fonts.gstatic.com; "
            + "img-src 'self' data:; "
            + "frame-ancestors 'none'; form-action 'self'; base-uri 'self'; object-src 'none'";

    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Content-Security-Policy", CSP);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Permissions-Policy", "geolocation=(), camera=(), microphone=()");
        if (request.isSecure()) response.setHeader("Strict-Transport-Security", "max-age=31536000");
        chain.doFilter(req, res);
    }
}
