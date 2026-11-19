package com.track.service;

import com.track.dao.ShipmentDAO;
import com.track.model.Shipment;
import com.track.model.ShipmentStatus;
import com.track.model.TrackingEvent;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class ShipmentService {
    private final ShipmentDAO dao = new ShipmentDAO();
    private final TrackingNumberGenerator generator = new TrackingNumberGenerator();

    public void create(Shipment shipment) {
        shipment.setTrackingNumber(generator.next());
        shipment.setStatus(ShipmentStatus.REGISTERED);
        shipment.getEvents().add(new TrackingEvent(
            shipment, null, ShipmentStatus.REGISTERED,
            shipment.getSenderAddress(), "Colis enregistré dans TRACK"));
        dao.save(shipment);
    }

    public List<Shipment> recent() { return dao.findRecent(10); }
    public long countAll() { return dao.countAll(); }
    // Regroupement des statuts pour le tableau de bord (à ajuster ici si besoin).
    // Les colis en échec, retournés ou annulés ne sont dans aucun des trois groupes.
    private static final EnumSet<ShipmentStatus> IN_TRANSIT = EnumSet.of(
        ShipmentStatus.SHIPPED, ShipmentStatus.IN_TRANSIT,
        ShipmentStatus.ARRIVED, ShipmentStatus.OUT_FOR_DELIVERY);
    private static final EnumSet<ShipmentStatus> DELIVERED = EnumSet.of(ShipmentStatus.DELIVERED);
    private static final EnumSet<ShipmentStatus> PENDING = EnumSet.of(
        ShipmentStatus.REGISTERED, ShipmentStatus.PENDING, ShipmentStatus.READY_FOR_PICKUP);

    public long countInTransit() { return dao.countByStatuses(IN_TRANSIT); }
    public long countDelivered() { return dao.countByStatuses(DELIVERED); }
    public long countPending() { return dao.countByStatuses(PENDING); }

    public Optional<Shipment> track(String number) {
        return dao.findByTrackingNumber(number);
    }
}
