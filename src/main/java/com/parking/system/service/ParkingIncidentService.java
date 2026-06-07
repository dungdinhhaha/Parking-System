package com.parking.system.service;

import com.parking.system.dto.request.CreateIncidentRequest;
import com.parking.system.dto.request.ResolveIncidentRequest;
import com.parking.system.dto.response.IncidentResponse;
import com.parking.system.enums.IncidentStatus;
import java.util.List;

public interface ParkingIncidentService {
    IncidentResponse create(String username, CreateIncidentRequest request);
    IncidentResponse close(String username, Long incidentId, ResolveIncidentRequest request);
    IncidentResponse get(Long incidentId);
    List<IncidentResponse> getAll();
    List<IncidentResponse> getBySession(Long parkingSessionId);
    List<IncidentResponse> getByStatus(IncidentStatus status);
}
