package com.parking.system.dto.request;

public record CreateIncidentRequest(Long parkingSessionId, String incidentType, String description) {
}
