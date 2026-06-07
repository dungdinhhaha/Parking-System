package com.parking.system.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardReportResponse {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final long totalBuildings;
    private final long totalFloors;
    private final long totalZones;
    private final long totalSlots;
    private final long totalReservations;
    private final long confirmedReservations;
    private final long usedReservations;
    private final long cancelledReservations;
    private final long activeSessions;
    private final long completedSessions;
    private final long unpaidSessions;
    private final RevenueReportResponse revenue;
    private final OccupancyReportResponse occupancy;
    private final IncidentReportResponse incidents;
}
