package com.track.model;

import static com.track.model.ShipmentStatus.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ShipmentStatusTest {
    @Test void terminalStatusesCannotChange() {
        for (ShipmentStatus to : values()) {
            assertFalse(DELIVERED.canTransitionTo(to));
            assertFalse(CANCELLED.canTransitionTo(to));
        }
    }

    @Test void nominalFlowIsAllowed() {
        assertTrue(REGISTERED.canTransitionTo(SHIPPED));
        assertTrue(SHIPPED.canTransitionTo(IN_TRANSIT));
        assertTrue(IN_TRANSIT.canTransitionTo(ARRIVED));
        assertTrue(ARRIVED.canTransitionTo(OUT_FOR_DELIVERY));
        assertTrue(OUT_FOR_DELIVERY.canTransitionTo(DELIVERED));
    }

    @Test void shortcutsAreRefused() {
        assertFalse(REGISTERED.canTransitionTo(DELIVERED));
        assertFalse(SHIPPED.canTransitionTo(DELIVERED));
        assertFalse(IN_TRANSIT.canTransitionTo(REGISTERED));
        assertFalse(REGISTERED.canTransitionTo(null));
    }

    @Test void checkpointsOnlyOnMovementStatuses() {
        assertTrue(IN_TRANSIT.canTransitionTo(IN_TRANSIT));
        assertTrue(ARRIVED.canTransitionTo(ARRIVED));
        assertTrue(OUT_FOR_DELIVERY.canTransitionTo(OUT_FOR_DELIVERY));
        assertFalse(REGISTERED.canTransitionTo(REGISTERED));
        assertFalse(SHIPPED.canTransitionTo(SHIPPED));
        assertFalse(DELIVERY_FAILED.canTransitionTo(DELIVERY_FAILED));
    }

    @Test void nextStatusesMatchTransitionRules() {
        for (ShipmentStatus from : values())
            for (ShipmentStatus to : values())
                assertEquals(from.canTransitionTo(to), from.getNextStatuses().contains(to), from + " -> " + to);
    }

    @Test void everyStatusHasFrenchLabelAndGroup() {
        for (ShipmentStatus s : values()) {
            assertNotNull(s.getLabel());
            assertFalse(s.getLabel().isBlank());
            assertNotNull(s.getGroup());
            assertNotNull(s.getTone());
        }
    }
}
