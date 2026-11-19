package com.track.service;

import java.security.SecureRandom;
import java.time.Year;

/** Numéro de suivi aléatoire (48 bits d'entropie, non devinable). La contrainte UNIQUE reste le garde-fou. */
public class TrackingNumberGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public String next() {
        StringBuilder sb = new StringBuilder("TRK-").append(Year.now().getValue()).append('-');
        for (int i = 0; i < 12; i++) sb.append(HEX[RANDOM.nextInt(16)]);
        return sb.toString();
    }
}
