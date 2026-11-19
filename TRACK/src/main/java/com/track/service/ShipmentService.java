package com.track.service;

import com.track.dao.ShipmentDAO;
import com.track.model.Shipment;
import com.track.model.ShipmentStatus;
import com.track.model.TrackingEvent;
import com.track.security.CurrentUser;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class ShipmentService {
    public static final int PAGE_SIZE = 20;
    private static final Pattern PHONE = Pattern.compile("[0-9+()\\s.\\-]{0,30}");
    private static final Pattern TRACKING = Pattern.compile("[A-Z0-9\\-]{1,40}");
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("99999999.99");

    private final ShipmentDAO dao = new ShipmentDAO();
    private final TrackingNumberGenerator generator = new TrackingNumberGenerator();

    /** Crée un colis rattaché à l'agence de l'utilisateur. @return le numéro de suivi. */
    public String create(Shipment shipment, CurrentUser user) {
        if (!user.getRole().canCreate()) throw new ForbiddenException("Vous n'avez pas le droit d'enregistrer un colis.");
        shipment.setSenderName(required(shipment.getSenderName(), 120, "Le nom de l'expéditeur est obligatoire (120 caractères max)."));
        shipment.setRecipientName(required(shipment.getRecipientName(), 120, "Le nom du destinataire est obligatoire (120 caractères max)."));
        shipment.setSenderPhone(phone(shipment.getSenderPhone(), "Téléphone de l'expéditeur invalide."));
        shipment.setRecipientPhone(phone(shipment.getRecipientPhone(), "Téléphone du destinataire invalide."));
        shipment.setSenderAddress(optional(shipment.getSenderAddress(), 255, "L'adresse de départ est trop longue (255 caractères max)."));
        shipment.setRecipientAddress(optional(shipment.getRecipientAddress(), 255, "L'adresse de destination est trop longue (255 caractères max)."));
        shipment.setDescription(optional(shipment.getDescription(), 500, "La description est trop longue (500 caractères max)."));
        BigDecimal w = shipment.getWeight();
        if (w != null && (w.signum() <= 0 || w.compareTo(MAX_WEIGHT) > 0))
            throw new IllegalArgumentException("Le poids doit être supérieur à zéro.");

        shipment.setAgencyId(user.getAgencyId());
        shipment.setTrackingNumber(generator.next());
        shipment.setStatus(ShipmentStatus.REGISTERED);
        shipment.getEvents().add(new TrackingEvent(shipment, null, ShipmentStatus.REGISTERED,
                user.getAgencyName(), "Colis enregistré dans TRACK", true, user.getId()));
        dao.save(shipment);
        return shipment.getTrackingNumber();
    }

    public Page<Shipment> search(CurrentUser user, String query, ShipmentStatus status, int page) {
        if (!user.getRole().seesAllAgencies() && user.getAgencyId() == null) return Page.empty(PAGE_SIZE);
        String q = query == null ? null : query.trim();
        if (q != null && q.length() > 80) q = q.substring(0, 80);
        return dao.search(user.getRole().seesAllAgencies(), user.getAgencyId(), q, status, page, PAGE_SIZE);
    }

    public List<Shipment> recent(CurrentUser user, int limit) {
        if (!user.getRole().seesAllAgencies() && user.getAgencyId() == null) return List.of();
        return dao.search(user.getRole().seesAllAgencies(), user.getAgencyId(), null, null, 0, limit).getItems();
    }

    public DashboardStats stats(CurrentUser user) {
        if (!user.getRole().seesAllAgencies() && user.getAgencyId() == null) return new DashboardStats();
        return DashboardStats.fromCounts(dao.countByStatus(user.getRole().seesAllAgencies(), user.getAgencyId()));
    }

    /** Statuts proposables pour ce colis à cet utilisateur (transition valide ET droit du rôle). */
    public List<ShipmentStatus> allowedNext(Shipment shipment, CurrentUser user) {
        return shipment.getStatus().getNextStatuses().stream()
                .filter(s -> user.getRole().canChangeTo(s)).toList();
    }

    /** @return false si le colis est introuvable (ou hors de l'agence de l'utilisateur). */
    public boolean changeStatus(String number, ShipmentStatus status, String location, String description,
                                boolean publicEvent, CurrentUser user) {
        if (status == null || !user.getRole().canChangeTo(status))
            throw new ForbiddenException("Votre rôle ne permet pas ce changement de statut.");
        String loc = required(location, 180, "La localité est obligatoire (180 caractères max).");
        String desc = optional(description, 500, "Le commentaire est trop long (500 caractères max).");
        if (desc == null) desc = "Mise à jour du suivi";
        return dao.changeStatus(number == null ? "" : number.trim(), status, loc, desc, publicEvent,
                user.getId(), s -> user.canAccessAgency(s.getAgencyId()));
    }

    /** Suivi public : renvoie le colis (avec ses événements) ou vide si le format est invalide. */
    public Optional<Shipment> track(String rawNumber) {
        if (rawNumber == null) return Optional.empty();
        String number = rawNumber.trim().toUpperCase();
        if (!TRACKING.matcher(number).matches()) return Optional.empty();
        return dao.findByTrackingNumber(number);
    }

    private static String required(String value, int max, String message) {
        if (value == null || value.isBlank() || value.trim().length() > max) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String optional(String value, int max, String message) {
        if (value == null || value.isBlank()) return null;
        if (value.trim().length() > max) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String phone(String value, String message) {
        if (value == null || value.isBlank()) return null;
        if (!PHONE.matcher(value.trim()).matches()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
