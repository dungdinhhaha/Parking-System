package com.parking.system.dto.response;

import com.parking.system.enums.IncidentStatus;
import com.parking.system.enums.IncidentType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncidentResponse {
    private final Long id;
    private final IncidentType incidentType;
    private final String description;
    private final IncidentStatus status;
    private final LocalDateTime resolvedAt;
    private final String resolutionNote;
    private final Long parkingSessionId;
    private final String ticketCode;
    private final String plateNumber;
    private final String vehicleType;
    private final String sessionStatus;
    private final Long plateRecognitionLogId;
    private final String reportedByUsername;
    private final String resolvedByUsername;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
