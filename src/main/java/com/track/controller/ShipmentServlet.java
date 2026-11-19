package com.track.controller;

import com.track.model.Shipment;
import com.track.service.ShipmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = {"/shipments", "/shipments/new"})
public class ShipmentServlet extends HttpServlet {
    private final ShipmentService service = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().endsWith("/new")) {
            request.getRequestDispatcher("/WEB-INF/views/shipments/new.jsp").forward(request, response);
            return;
        }
        request.setAttribute("shipments", service.recent());
        request.getRequestDispatcher("/WEB-INF/views/shipments/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            Shipment s = new Shipment();
            s.setSenderName(request.getParameter("senderName"));
            s.setSenderPhone(request.getParameter("senderPhone"));
            s.setSenderAddress(request.getParameter("senderAddress"));
            s.setRecipientName(request.getParameter("recipientName"));
            s.setRecipientPhone(request.getParameter("recipientPhone"));
            s.setRecipientAddress(request.getParameter("recipientAddress"));
            s.setDescription(request.getParameter("description"));
            String weight = request.getParameter("weight");
            if (weight != null && !weight.isBlank()) s.setWeight(new BigDecimal(weight));
            service.create(s);
            response.sendRedirect(request.getContextPath() + "/shipments");
        } catch (RuntimeException ex) {
            request.setAttribute("error", "Impossible d'enregistrer le colis. Vérifie les informations et la base de données.");
            request.getRequestDispatcher("/WEB-INF/views/shipments/new.jsp").forward(request, response);
        }
    }
}
