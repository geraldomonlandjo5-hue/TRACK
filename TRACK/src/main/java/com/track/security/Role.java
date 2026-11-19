package com.track.security;

import com.track.model.ShipmentStatus;
import java.util.EnumSet;
import java.util.Set;

/**
 * Rôles applicatifs (noms identiques à la table {@code roles}) et leurs permissions.
 * Principe du moindre privilège : un rôle inconnu n'a aucun droit.
 */
public enum Role {
    SUPER_ADMIN("Super administrateur", true, true, EnumSet.allOf(ShipmentStatus.class)),
    ADMIN("Administrateur", true, true, EnumSet.allOf(ShipmentStatus.class)),
    RESPONSABLE_AGENCE("Responsable d'agence", false, true, EnumSet.allOf(ShipmentStatus.class)),
    AGENT("Agent", false, true, EnumSet.allOf(ShipmentStatus.class)),
    LIVREUR("Livreur", false, false, EnumSet.of(ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.DELIVERED,
            ShipmentStatus.DELIVERY_FAILED, ShipmentStatus.RETURNED)),
    /** Rôle inconnu / non reconnu : aucun droit. */
    NONE("Aucun droit", false, false, EnumSet.noneOf(ShipmentStatus.class));

    private final String label;
    private final boolean allAgencies;
    private final boolean create;
    private final Set<ShipmentStatus> settable;

    Role(String label, boolean allAgencies, boolean create, Set<ShipmentStatus> settable) {
        this.label = label;
        this.allAgencies = allAgencies;
        this.create = create;
        this.settable = settable;
    }

    public static Role fromName(String name) {
        if (name == null) return NONE;
        try { return valueOf(name); } catch (IllegalArgumentException ex) { return NONE; }
    }

    public String getLabel() { return label; }
    /** Voit les colis de toutes les agences. */
    public boolean seesAllAgencies() { return allAgencies; }
    public boolean canCreate() { return create; }
    public boolean canChangeTo(ShipmentStatus status) { return settable.contains(status); }
}
