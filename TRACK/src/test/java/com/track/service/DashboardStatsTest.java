package com.track.service;

import static org.junit.jupiter.api.Assertions.*;

import com.track.model.ShipmentStatus;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DashboardStatsTest {
    @Test void groupsAlwaysSumToTotal() {
        Map<ShipmentStatus, Long> counts = new EnumMap<>(ShipmentStatus.class);
        long expected = 0;
        for (ShipmentStatus s : ShipmentStatus.values()) { counts.put(s, (long) (s.ordinal() + 1)); expected += s.ordinal() + 1; }
        DashboardStats st = DashboardStats.fromCounts(counts);
        assertEquals(expected, st.getTotal());
        assertEquals(st.getTotal(), st.getPending() + st.getInProgress() + st.getDelivered() + st.getIncidents());
    }

    @Test void specificStatusesLandInTheRightGroup() {
        DashboardStats st = DashboardStats.fromCounts(Map.of(
                ShipmentStatus.REGISTERED, 2L, ShipmentStatus.ARRIVED, 3L,
                ShipmentStatus.READY_FOR_PICKUP, 1L, ShipmentStatus.DELIVERED, 5L,
                ShipmentStatus.DELIVERY_FAILED, 4L));
        assertEquals(2, st.getPending());
        assertEquals(4, st.getInProgress());
        assertEquals(5, st.getDelivered());
        assertEquals(4, st.getIncidents());
    }

    @Test void emptyCountsGiveZeros() {
        assertEquals(0, DashboardStats.fromCounts(Map.of()).getTotal());
    }
}
