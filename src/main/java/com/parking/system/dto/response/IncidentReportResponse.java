package com.parking.system.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncidentReportResponse {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final long totalIncidents;
    private final long openIncidents;
    private final long processingIncidents;
    private final long resolvedIncidents;
    private final long cancelledIncidents;
    private final List<IncidentCountResponse> incidentBreakdown;
    private final List<IncidentResponse> recentIncidents;
}
