package com.track.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipments_tracking_number", columnList = "tracking_number", unique = true),
    @Index(name = "idx_shipments_status", columnList = "status")
})
public class Shipment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 40)
    private String trackingNumber;

    /** Agence propriétaire du colis (null = colis de l'administration centrale). */
    @Column(name = "agency_id", length = 36)
    private String agencyId;

    @Column(name = "sender_name", nullable = false, length = 120)
    private String senderName;

    @Column(name = "sender_phone", length = 30)
    private String senderPhone;

    @Column(name = "sender_address", length = 255)
    private String senderAddress;

    @Column(name = "recipient_name", nullable = false, length = 120)
    private String recipientName;

    @Column(name = "recipient_phone", length = 30)
    private String recipientPhone;

    @Column(name = "recipient_address", length = 255)
    private String recipientAddress;

    @Column(length = 500)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus status = ShipmentStatus.REGISTERED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("createdAt ASC")
    private List<TrackingEvent> events = new ArrayList<>();

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getAgencyId() { return agencyId; }
    public void setAgencyId(String agencyId) { this.agencyId = agencyId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }
    public String getSenderAddress() { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    public String getRecipientAddress() { return recipientAddress; }
    public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<TrackingEvent> getEvents() { return events; }

    /** Événements visibles par le client sur la page publique (lecture seule, non persisté). */
    public List<TrackingEvent> getPublicEvents() {
        return events.stream().filter(TrackingEvent::isPublicEvent).toList();
    }
}
