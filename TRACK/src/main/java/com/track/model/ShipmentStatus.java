package com.track.model;

import java.util.List;

/**
 * Statuts d'un colis, avec libellé français, couleur d'affichage, groupe de statistiques
 * et règles de transition (source unique de vérité pour la machine à états).
 */
public enum ShipmentStatus {
    REGISTERED("Enregistré", "neutral", Group.PENDING),
    PENDING("En attente", "wait", Group.PENDING),
    SHIPPED("Expédié", "transit", Group.IN_PROGRESS),
    IN_TRANSIT("En transit", "transit", Group.IN_PROGRESS),
    ARRIVED("Arrivé", "transit", Group.IN_PROGRESS),
    READY_FOR_PICKUP("Prêt au retrait", "wait", Group.IN_PROGRESS),
    OUT_FOR_DELIVERY("En cours de livraison", "transit", Group.IN_PROGRESS),
    DELIVERED("Livré", "done", Group.DELIVERED),
    DELIVERY_FAILED("Échec de livraison", "alert", Group.INCIDENT),
    RETURNED("Retourné", "off", Group.INCIDENT),
    CANCELLED("Annulé", "off", Group.INCIDENT);

    /** Regroupement pour le tableau de bord : la somme des groupes égale le total. */
    public enum Group { PENDING, IN_PROGRESS, DELIVERED, INCIDENT }

    private final String label;
    private final String tone;
    private final Group group;

    ShipmentStatus(String label, String tone, Group group) {
        this.label = label;
        this.tone = tone;
        this.group = group;
    }

    public String getLabel() { return label; }
    public String getTone() { return tone; }
    public Group getGroup() { return group; }

    public boolean isTerminal() { return this == DELIVERED || this == CANCELLED; }

    /**
     * Statuts « de mouvement » : on peut enregistrer plusieurs points de passage
     * sans changer de statut (ex. un 2e scan « En transit » dans une autre ville).
     */
    public boolean allowsCheckpoint() {
        return this == IN_TRANSIT || this == ARRIVED || this == OUT_FOR_DELIVERY;
    }

    public boolean canTransitionTo(ShipmentStatus to) {
        if (to == null || isTerminal()) return false;
        if (this == to) return allowsCheckpoint();
        return switch (this) {
            case REGISTERED, PENDING -> to == SHIPPED || to == CANCELLED || to == PENDING;
            case SHIPPED -> to == IN_TRANSIT || to == ARRIVED;
            case IN_TRANSIT -> to == ARRIVED || to == DELIVERY_FAILED;
            case ARRIVED -> to == READY_FOR_PICKUP || to == OUT_FOR_DELIVERY || to == IN_TRANSIT;
            case READY_FOR_PICKUP -> to == DELIVERED || to == RETURNED;
            case OUT_FOR_DELIVERY -> to == DELIVERED || to == DELIVERY_FAILED || to == RETURNED;
            case DELIVERY_FAILED -> to == OUT_FOR_DELIVERY || to == READY_FOR_PICKUP || to == RETURNED;
            case RETURNED -> to == CANCELLED;
            default -> false;
        };
    }

    /** Statuts proposés à partir de celui-ci (inclut lui-même si point de passage possible). */
    public List<ShipmentStatus> getNextStatuses() {
        return java.util.Arrays.stream(values()).filter(this::canTransitionTo).toList();
    }
}
