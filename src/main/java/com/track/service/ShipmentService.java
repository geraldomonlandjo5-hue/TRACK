package com.track.service;

import com.track.dao.ShipmentDAO;
import com.track.model.Shipment;
import com.track.model.ShipmentStatus;
import com.track.model.TrackingEvent;
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
    public Optional<Shipment> track(String number) {
        return dao.findByTrackingNumber(number);
    }
}
