package com.track.dao;

import com.track.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class UserDAO {
    public record UserAccount(String id, String email, String fullName, String role, String passwordHash,
                              String agencyId, String agencyName) {}

    /** Utilisateur actif (et dont l'agence éventuelle est active). */
    public Optional<UserAccount> findActiveByEmail(String email) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            var rows = em.createNativeQuery("""
                SELECT u.id, u.email, u.full_name, r.name, u.password_hash, u.agency_id,
                       COALESCE(a.name, 'Administration centrale')
                FROM users u JOIN roles r ON r.id = u.role_id
                LEFT JOIN agencies a ON a.id = u.agency_id
                WHERE lower(u.email) = lower(?1) AND u.is_active = TRUE
                  AND (a.id IS NULL OR a.is_active = TRUE)
                """).setParameter(1, email).getResultList();
            if (rows.isEmpty()) return Optional.empty();
            Object[] row = (Object[]) rows.get(0);
            return Optional.of(new UserAccount((String) row[0], (String) row[1], (String) row[2], (String) row[3],
                    (String) row[4], (String) row[5], (String) row[6]));
        } finally { em.close(); }
    }

    /** Le compte (et son agence) est-il toujours actif ? Utilisé pour révoquer les sessions en cours. */
    public boolean isStillActive(String userId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            return !em.createNativeQuery("""
                SELECT 1 FROM users u LEFT JOIN agencies a ON a.id = u.agency_id
                WHERE u.id = ?1 AND u.is_active = TRUE AND (a.id IS NULL OR a.is_active = TRUE)
                """).setParameter(1, userId).getResultList().isEmpty();
        } finally { em.close(); }
    }

    public void touchLastLogin(String userId) {
        EntityManager em = JpaUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?1")
                    .setParameter(1, userId).executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }
}
