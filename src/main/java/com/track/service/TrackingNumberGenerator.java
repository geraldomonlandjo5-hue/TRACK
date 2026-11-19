package com.track.service;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Générateur de démonstration pour le squelette.
 * En production, remplacer par une séquence PostgreSQL ou une stratégie transactionnelle
 * afin de garantir l'unicité même avec plusieurs instances de l'application.
 */
public class TrackingNumberGenerator {
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    public String next() {
        return "TRK-" + Year.now().getValue() + "-"
                + String.format("%06d", SEQUENCE.getAndIncrement());
    }
}
