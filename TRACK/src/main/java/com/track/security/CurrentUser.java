package com.track.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Objects;

/** Utilisateur connecté, stocké en session (attribut {@code currentUser}). */
public final class CurrentUser implements Serializable {
    public static final String SESSION_KEY = "currentUser";

    private final String id;
    private final String email;
    private final String name;
    private final Role role;
    private final String agencyId;
    private final String agencyName;

    public CurrentUser(String id, String email, String name, Role role, String agencyId, String agencyName) {
        this.id = id; this.email = email; this.name = name; this.role = role;
        this.agencyId = agencyId; this.agencyName = agencyName;
    }

    public static CurrentUser from(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (CurrentUser) session.getAttribute(SESSION_KEY);
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getAgencyId() { return agencyId; }
    public String getAgencyName() { return agencyName; }

    /** Peut-on accéder à un colis de cette agence ? Un utilisateur sans agence (hors admin) ne voit rien. */
    public boolean canAccessAgency(String shipmentAgencyId) {
        if (role.seesAllAgencies()) return true;
        return agencyId != null && Objects.equals(agencyId, shipmentAgencyId);
    }
}
