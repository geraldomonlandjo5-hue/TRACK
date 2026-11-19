package com.track.dao;

import com.track.model.Shipment;
import com.track.model.ShipmentStatus;
import com.track.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        } finally {
            em.close();
        }
    }

    public List<Shipment> findRecent(int limit) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Shipment s ORDER BY s.createdAt DESC", Shipment.class)
                .setMaxResults(limit).getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Shipment s", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countByStatuses(Collection<ShipmentStatus> statuses) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(s) FROM Shipment s WHERE s.status IN (:statuses)", Long.class)
                .setParameter("statuses", statuses).getSingleResult();
        } finally {
            em.close();
        }
    }

    public Optional<Shipment> findByTrackingNumber(String number) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            List<Shipment> result = em.createQuery(
                "SELECT DISTINCT s FROM Shipment s LEFT JOIN FETCH s.events WHERE s.trackingNumber = :number",
                Shipment.class).setParameter("number", number).getResultList();
            return result.stream().findFirst();
        } finally {
            em.close();
        }
    }

    public Optional<Shipment> findById(String id) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Shipment.class, id));
        } finally {
            em.close();
        }
    }
}
