package com.track.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("TrackPU");

    private JpaUtil() {}

    public static EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }
}
