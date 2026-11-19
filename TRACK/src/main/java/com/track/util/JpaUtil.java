package com.track.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Accès à l'EntityManagerFactory. Création paresseuse : si la base est indisponible au premier appel,
 * l'erreur est signalée (IllegalStateException) mais n'est pas mémorisée, donc le prochain appel retente.
 */
public final class JpaUtil {
    private static volatile EntityManagerFactory emf;

    private JpaUtil() {}

    public static EntityManager createEntityManager() {
        EntityManagerFactory factory = emf;
        if (factory == null) {
            synchronized (JpaUtil.class) {
                factory = emf;
                if (factory == null) {
                    try {
                        factory = Persistence.createEntityManagerFactory("TrackPU");
                    } catch (RuntimeException | LinkageError ex) {
                        throw new IllegalStateException("Base de données indisponible", ex);
                    }
                    emf = factory;
                }
            }
        }
        return factory.createEntityManager();
    }

    /** Appelé à l'arrêt de l'application (voir AppListener). */
    public static synchronized void shutdown() {
        if (emf != null) {
            try { if (emf.isOpen()) emf.close(); } finally { emf = null; }
        }
    }
}
