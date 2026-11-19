package com.track.controller;

import com.track.service.ShipmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final ShipmentService service = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("totalShipments", service.countAll());
        request.setAttribute("inTransitShipments", service.countInTransit());
        request.setAttribute("deliveredShipments", service.countDelivered());
        request.setAttribute("pendingShipments", service.countPending());
        request.setAttribute("recentShipments", service.recent());
        request.getRequestDispatcher("/WEB-INF/views/dashboard/index.jsp").forward(request, response);
    }
}
