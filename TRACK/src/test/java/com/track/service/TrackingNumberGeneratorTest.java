package com.track.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrackingNumberGeneratorTest {
    @Test void formatIsStable() {
        assertTrue(new TrackingNumberGenerator().next().matches("TRK-\\d{4}-[0-9A-F]{12}"));
    }

    @Test void numbersDoNotCollideInPractice() {
        TrackingNumberGenerator g = new TrackingNumberGenerator();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 5000; i++) assertTrue(seen.add(g.next()));
    }
}
