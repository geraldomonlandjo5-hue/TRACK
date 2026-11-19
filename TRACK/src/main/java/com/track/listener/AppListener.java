package com.track.listener;

import com.track.util.JpaUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        SessionCookieConfig cookie = event.getServletContext().getSessionCookieConfig();
        cookie.setHttpOnly(true);
        // Activer en production derrière HTTPS : -Dtrack.secure.cookies=true ou variable TRACK_SECURE_COOKIES=true
        boolean secure = Boolean.getBoolean("track.secure.cookies")
                || "true".equalsIgnoreCase(System.getenv("TRACK_SECURE_COOKIES"));
        cookie.setSecure(secure);
        try {
            cookie.setAttribute("SameSite", "Lax");
        } catch (RuntimeException ex) {
            event.getServletContext().log("SameSite non appliqué sur le cookie de session", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        JpaUtil.shutdown();
    }
}
