package com.track.controller;

import com.track.service.ShipmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/track")
public class TrackingServlet extends HttpServlet {
    private final ShipmentService service = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String number = request.getParameter("number");
        if (number != null && !number.isBlank()) {
            service.track(number.trim().toUpperCase())
                   .ifPresent(s -> request.setAttribute("shipment", s));
            request.setAttribute("searched", true);
        }
        request.getRequestDispatcher("/WEB-INF/views/public/track.jsp").forward(request, response);
    }
}
