package com.track.controller;

import com.track.model.Shipment;
import com.track.model.ShipmentStatus;
import com.track.security.CurrentUser;
import com.track.service.ForbiddenException;
import com.track.service.Page;
import com.track.service.ShipmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/shipments", "/shipments/new"})
public class ShipmentServlet extends HttpServlet {
    private final ShipmentService service = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CurrentUser user = CurrentUser.from(request);
        if (request.getServletPath().endsWith("/new")) {
            if (!user.getRole().canCreate()) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
            request.getRequestDispatcher("/WEB-INF/views/shipments/new.jsp").forward(request, response);
            return;
        }
        String q = request.getParameter("q");
        ShipmentStatus filter = parseStatus(request.getParameter("status"));
        Page<Shipment> result = service.search(user, q, filter, parseInt(request.getParameter("page")));
        Map<String, List<ShipmentStatus>> allowed = new LinkedHashMap<>();
        for (Shipment s : result.getItems()) allowed.put(s.getTrackingNumber(), service.allowedNext(s, user));
        request.setAttribute("result", result);
        request.setAttribute("allowed", allowed);
        request.setAttribute("q", q == null ? "" : q.trim());
        request.setAttribute("statusFilter", filter == null ? "" : filter.name());
        request.setAttribute("statuses", ShipmentStatus.values());
        request.getRequestDispatcher("/WEB-INF/views/shipments/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        CurrentUser user = CurrentUser.from(request);
        try {
            if ("status".equals(request.getParameter("action"))) changeStatus(request, response, user);
            else createShipment(request, response, user);
        } catch (ForbiddenException ex) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void changeStatus(HttpServletRequest request, HttpServletResponse response, CurrentUser user)
            throws IOException {
        String flash;
        try {
            ShipmentStatus status = parseStatus(request.getParameter("status"));
            if (status == null) throw new IllegalArgumentException("Statut invalide.");
            boolean changed = service.changeStatus(request.getParameter("trackingNumber"), status,
                    request.getParameter("location"), request.getParameter("description"),
                    request.getParameter("publicEvent") != null, user);
            flash = changed ? "Suivi du colis mis à jour." : "Colis introuvable.";
        } catch (ForbiddenException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            flash = ex.getMessage();
        } catch (RuntimeException ex) {
            getServletContext().log("Mise à jour du statut impossible", ex);
            flash = "Mise à jour impossible.";
        }
        request.getSession().setAttribute("flash", flash);
        response.sendRedirect(request.getContextPath() + "/shipments" + returnQuery(request));
    }

    private void createShipment(HttpServletRequest request, HttpServletResponse response, CurrentUser user)
            throws ServletException, IOException {
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
            if (weight != null && !weight.isBlank()) {
                try { s.setWeight(new BigDecimal(weight.trim())); }
                catch (NumberFormatException ex) { throw new IllegalArgumentException("Poids invalide."); }
            }
            String number = service.create(s, user);
            request.getSession().setAttribute("flash", "Colis enregistré : " + number);
            response.sendRedirect(request.getContextPath() + "/shipments");
        } catch (ForbiddenException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            showError(request, response, ex.getMessage());
        } catch (RuntimeException ex) {
            getServletContext().log("Enregistrement du colis impossible", ex);
            showError(request, response, "Impossible d'enregistrer le colis. Réessaie ou contacte l'administrateur.");
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.getRequestDispatcher("/WEB-INF/views/shipments/new.jsp").forward(request, response);
    }

    /** Conserve recherche, filtre et page après une mise à jour (valeurs revalidées, jamais recopiées telles quelles). */
    private static String returnQuery(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        String q = request.getParameter("rq");
        if (q != null && !q.isBlank() && q.length() <= 80)
            sb.append(sb.isEmpty() ? '?' : '&').append("q=").append(URLEncoder.encode(q.trim(), StandardCharsets.UTF_8));
        ShipmentStatus st = parseStatus(request.getParameter("rs"));
        if (st != null) sb.append(sb.isEmpty() ? '?' : '&').append("status=").append(st.name());
        int p = parseInt(request.getParameter("rp"));
        if (p > 0) sb.append(sb.isEmpty() ? '?' : '&').append("page=").append(p);
        return sb.toString();
    }

    private static ShipmentStatus parseStatus(String value) {
        if (value == null || value.isBlank()) return null;
        try { return ShipmentStatus.valueOf(value); } catch (IllegalArgumentException ex) { return null; }
    }

    private static int parseInt(String value) {
        try { return value == null ? 0 : Math.max(0, Integer.parseInt(value.trim())); }
        catch (NumberFormatException ex) { return 0; }
    }
}
