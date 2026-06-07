package com.parking.system.service.report;

import com.parking.system.dto.response.IncidentCountResponse;
import com.parking.system.dto.response.IncidentReportResponse;
import com.parking.system.entity.ParkingIncident;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.repository.ParkingIncidentRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IncidentReportCalculator {

    private final ParkingIncidentRepository incidentRepository;

    public IncidentReportCalculator(ParkingIncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public IncidentReportResponse calculate(LocalDateTime from, LocalDateTime to) {
        List<ParkingIncident> incidents = incidentRepository.findAll().stream()
                .filter(incident -> isWithin(incident.getCreatedAt(), from, to))
                .sorted(Comparator.comparing(ParkingIncident::getCreatedAt).reversed())
                .toList();

        return IncidentReportResponse.builder()
                .from(from)
                .to(to)
                .totalIncidents(incidents.size())
                .openIncidents(countByStatus(incidents, IncidentStatus.OPEN))
                .processingIncidents(countByStatus(incidents, IncidentStatus.PROCESSING))
                .resolvedIncidents(countByStatus(incidents, IncidentStatus.RESOLVED))
                .cancelledIncidents(countByStatus(incidents, IncidentStatus.CANCELLED))
                .incidentBreakdown(java.util.Arrays.stream(com.parking.system.enums.IncidentType.values())
                        .map(type -> IncidentCountResponse.builder()
                                .incidentType(type)
                                .count(incidents.stream().filter(incident -> incident.getIncidentType() == type).count())
                                .build())
                        .toList())
                .recentIncidents(incidents.stream().limit(10).map(this::toResponse).toList())
                .build();
    }

    private long countByStatus(List<ParkingIncident> incidents, IncidentStatus status) {
        return incidents.stream().filter(incident -> incident.getStatus() == status).count();
    }

    private boolean isWithin(LocalDateTime value, LocalDateTime from, LocalDateTime to) {
        if (value == null) {
            return false;
        }
        return (from == null || !value.isBefore(from)) && (to == null || !value.isAfter(to));
    }

    private com.parking.system.dto.response.IncidentResponse toResponse(ParkingIncident incident) {
        return com.parking.system.dto.response.IncidentResponse.builder()
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
}
