package com.track.controller;

import com.track.security.RateLimiter;
import com.track.service.ShipmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/track")
public class TrackingServlet extends HttpServlet {
    /** 30 recherches par minute et par adresse IP (derrière un proxy, activer le transfert de l'IP cliente). */
    private static final RateLimiter LOOKUPS = new RateLimiter(30, 60_000L);
    private final ShipmentService service = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String number = request.getParameter("number");
        if (number != null && !number.isBlank()) {
            if (!LOOKUPS.allow(request.getRemoteAddr())) {
                response.setStatus(429);
                request.setAttribute("rateLimited", true);
            } else {
                service.track(number).ifPresent(s -> request.setAttribute("shipment", s));
                request.setAttribute("searched", true);
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/public/track.jsp").forward(request, response);
    }
}
