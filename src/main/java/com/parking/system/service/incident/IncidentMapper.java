package com.parking.system.service.incident;

import com.parking.system.dto.response.IncidentResponse;
import com.parking.system.entity.ParkingIncident;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponse toResponse(ParkingIncident incident) {
        if (incident == null) {
            return null;
        }
        return IncidentResponse.builder()
                .id(incident.getId())
                .incidentType(incident.getIncidentType())
                .description(incident.getDescription())
                .status(incident.getStatus())
                .resolvedAt(incident.getResolvedAt())
                .resolutionNote(incident.getResolutionNote())
                .parkingSessionId(incident.getParkingSession() == null ? null : incident.getParkingSession().getId())
                .ticketCode(incident.getParkingSession() == null ? null : incident.getParkingSession().getTicketCode())
                .plateNumber(incident.getParkingSession() == null ? null : incident.getParkingSession().getPlateNumber())
                .vehicleType(incident.getParkingSession() == null || incident.getParkingSession().getVehicleType() == null
                        ? null
                        : incident.getParkingSession().getVehicleType().name())
                .sessionStatus(incident.getParkingSession() == null || incident.getParkingSession().getStatus() == null
                        ? null
                        : incident.getParkingSession().getStatus().name())
                .plateRecognitionLogId(incident.getPlateRecognitionLog() == null ? null : incident.getPlateRecognitionLog().getId())
                .reportedByUsername(incident.getReportedBy() == null ? null : incident.getReportedBy().getUsername())
                .resolvedByUsername(incident.getResolvedBy() == null ? null : incident.getResolvedBy().getUsername())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }

    public List<IncidentResponse> toResponses(List<ParkingIncident> incidents) {
        return incidents.stream().map(this::toResponse).toList();
    }
}
