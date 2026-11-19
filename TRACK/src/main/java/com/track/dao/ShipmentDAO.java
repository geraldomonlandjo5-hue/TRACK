package com.track.dao;

import com.track.model.Shipment;
import com.track.model.ShipmentStatus;
import com.track.model.TrackingEvent;
import com.track.service.Page;
import com.track.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import java.util.*;
import java.util.function.Predicate;

public class ShipmentDAO {

    public void save(Shipment shipment) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(shipment);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    /**
     * Recherche paginée.
     * @param allAgencies true = pas de filtre d'agence ; sinon filtre sur {@code agencyId}
     * @param query texte libre (numéro de suivi, expéditeur, destinataire), peut être null
     * @param status filtre de statut, peut être null
     */
    public Page<Shipment> search(boolean allAgencies, String agencyId, String query,
                                 ShipmentStatus status, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (!allAgencies) where.append(" AND s.agencyId = :agency");
        if (status != null) where.append(" AND s.status = :status");
        boolean hasQuery = query != null && !query.isBlank();
        if (hasQuery) where.append(" AND (LOWER(s.trackingNumber) LIKE :q ESCAPE '!'"
                + " OR LOWER(s.senderName) LIKE :q ESCAPE '!' OR LOWER(s.recipientName) LIKE :q ESCAPE '!')");

        EntityManager em = JpaUtil.createEntityManager();
        try {
            TypedQuery<Long> count = em.createQuery("SELECT COUNT(s) FROM Shipment s" + where, Long.class);
            TypedQuery<Shipment> list = em.createQuery(
                    "SELECT s FROM Shipment s" + where + " ORDER BY s.createdAt DESC", Shipment.class);
            for (TypedQuery<?> q : List.of(count, list)) {
                if (!allAgencies) q.setParameter("agency", agencyId);
                if (status != null) q.setParameter("status", status);
                if (hasQuery) q.setParameter("q", "%" + escapeLike(query.trim().toLowerCase()) + "%");
            }
            long total = count.getSingleResult();
            int lastPage = (int) Math.max(0, (total + size - 1) / size - 1);
            int safePage = Math.min(Math.max(page, 0), lastPage);
            List<Shipment> items = list.setFirstResult(safePage * size).setMaxResults(size).getResultList();
            return new Page<>(items, safePage, size, total);
        } finally { em.close(); }
    }

    /** Un seul GROUP BY pour tout le tableau de bord. */
    public Map<ShipmentStatus, Long> countByStatus(boolean allAgencies, String agencyId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            String jpql = "SELECT s.status, COUNT(s) FROM Shipment s"
                    + (allAgencies ? "" : " WHERE s.agencyId = :agency") + " GROUP BY s.status";
            TypedQuery<Object[]> q = em.createQuery(jpql, Object[].class);
            if (!allAgencies) q.setParameter("agency", agencyId);
            Map<ShipmentStatus, Long> result = new EnumMap<>(ShipmentStatus.class);
            for (Object[] row : q.getResultList()) result.put((ShipmentStatus) row[0], (Long) row[1]);
            return result;
        } finally { em.close(); }
    }

    public Optional<Shipment> findByTrackingNumber(String number) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT s FROM Shipment s LEFT JOIN FETCH s.events WHERE s.trackingNumber = :number",
                    Shipment.class).setParameter("number", number).getResultList().stream().findFirst();
        } finally { em.close(); }
    }

    /**
     * Change le statut (ou ajoute un point de passage) dans une transaction, sous verrou pessimiste
     * pour éviter deux mises à jour concurrentes à partir du même état.
     *
     * @param canAccess contrôle d'accès (agence) ; un colis inaccessible est traité comme introuvable
     * @return false si le colis est introuvable ou inaccessible
     * @throws IllegalArgumentException si la transition n'est pas autorisée
     */
    public boolean changeStatus(String trackingNumber, ShipmentStatus next, String location, String description,
                                boolean publicEvent, String userId, Predicate<Shipment> canAccess) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            List<Shipment> found = em.createQuery("SELECT s FROM Shipment s WHERE s.trackingNumber = :n", Shipment.class)
                    .setParameter("n", trackingNumber).setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setMaxResults(1).getResultList();
            if (found.isEmpty() || !canAccess.test(found.get(0))) {
                em.getTransaction().rollback();
                return false;
            }
            Shipment shipment = found.get(0);
            ShipmentStatus previous = shipment.getStatus();
            if (!previous.canTransitionTo(next))
                throw new IllegalArgumentException("Transition de statut non autorisée : "
                        + previous.getLabel() + " → " + next.getLabel() + ".");
            shipment.setStatus(next);
            shipment.getEvents().add(new TrackingEvent(shipment, previous, next, location, description,
                    publicEvent, userId));
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    private static String escapeLike(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
