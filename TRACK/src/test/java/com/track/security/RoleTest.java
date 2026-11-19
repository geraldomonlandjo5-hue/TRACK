package com.track.security;

import static org.junit.jupiter.api.Assertions.*;

import com.track.model.ShipmentStatus;
import org.junit.jupiter.api.Test;

class RoleTest {
    @Test void unknownRoleHasNoRights() {
        Role r = Role.fromName("PIRATE");
        assertEquals(Role.NONE, r);
        assertEquals(Role.NONE, Role.fromName(null));
        assertFalse(r.canCreate());
        for (ShipmentStatus s : ShipmentStatus.values()) assertFalse(r.canChangeTo(s));
    }

    @Test void courierCanOnlyHandleDeliveries() {
        Role r = Role.LIVREUR;
        assertFalse(r.canCreate());
        assertTrue(r.canChangeTo(ShipmentStatus.DELIVERED));
        assertTrue(r.canChangeTo(ShipmentStatus.DELIVERY_FAILED));
        assertFalse(r.canChangeTo(ShipmentStatus.CANCELLED));
        assertFalse(r.canChangeTo(ShipmentStatus.SHIPPED));
    }

    @Test void onlyAdminsSeeAllAgencies() {
        assertTrue(Role.SUPER_ADMIN.seesAllAgencies());
        assertTrue(Role.ADMIN.seesAllAgencies());
        assertFalse(Role.AGENT.seesAllAgencies());
        assertFalse(Role.RESPONSABLE_AGENCE.seesAllAgencies());
    }

    @Test void agencyScopingOnCurrentUser() {
        CurrentUser agent = new CurrentUser("u1", "a@x", "Agent", Role.AGENT, "ag1", "Agence 1");
        assertTrue(agent.canAccessAgency("ag1"));
        assertFalse(agent.canAccessAgency("ag2"));
        assertFalse(agent.canAccessAgency(null), "un colis sans agence n'est pas visible d'un agent");

        CurrentUser orphan = new CurrentUser("u2", "b@x", "Sans agence", Role.AGENT, null, "—");
        assertFalse(orphan.canAccessAgency(null), "agent sans agence : aucun accès");

        CurrentUser admin = new CurrentUser("u3", "c@x", "Admin", Role.ADMIN, null, "Centrale");
        assertTrue(admin.canAccessAgency("ag1"));
        assertTrue(admin.canAccessAgency(null));
    }
}
