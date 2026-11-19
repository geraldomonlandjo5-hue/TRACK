package com.track.service;

import com.track.model.ShipmentStatus;
import java.util.Map;

/** Compteurs du tableau de bord : pending + inProgress + delivered + incidents = total. */
public final class DashboardStats {
    private long total, pending, inProgress, delivered, incidents;

    public static DashboardStats fromCounts(Map<ShipmentStatus, Long> counts) {
        DashboardStats s = new DashboardStats();
        counts.forEach((status, n) -> {
            s.total += n;
            switch (status.getGroup()) {
                case PENDING -> s.pending += n;
                case IN_PROGRESS -> s.inProgress += n;
                case DELIVERED -> s.delivered += n;
                case INCIDENT -> s.incidents += n;
            }
        });
        return s;
    }

    public long getTotal() { return total; }
    public long getPending() { return pending; }
    public long getInProgress() { return inProgress; }
    public long getDelivered() { return delivered; }
    public long getIncidents() { return incidents; }
}
