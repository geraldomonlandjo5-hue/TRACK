package com.track.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
public class TrackingEvent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private ShipmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private ShipmentStatus newStatus;

    @Column(length = 180)
    private String location;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TrackingEvent() {}
    public TrackingEvent(Shipment shipment, ShipmentStatus previousStatus,
                         ShipmentStatus newStatus, String location, String description) {
        this.shipment = shipment;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.location = location;
        this.description = description;
    }
    public String getId() { return id; }
    public Shipment getShipment() { return shipment; }
    public ShipmentStatus getPreviousStatus() { return previousStatus; }
    public ShipmentStatus getNewStatus() { return newStatus; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
