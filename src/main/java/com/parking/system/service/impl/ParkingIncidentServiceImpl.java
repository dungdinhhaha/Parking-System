package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateIncidentRequest;
import com.parking.system.dto.request.ResolveIncidentRequest;
import com.parking.system.dto.response.IncidentResponse;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.facade.IncidentFacade;
import com.parking.system.service.ParkingIncidentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingIncidentServiceImpl implements ParkingIncidentService {

    private final IncidentFacade incidentFacade;

    @Override
    public IncidentResponse create(String username, CreateIncidentRequest request) {
        return incidentFacade.create(username, request);
    }

    @Override
    public IncidentResponse close(String username, Long incidentId, ResolveIncidentRequest request) {
        return incidentFacade.close(username, incidentId, request);
    }

    @Override
    public IncidentResponse get(Long incidentId) {
        return incidentFacade.get(incidentId);
    }

    @Override
    public List<IncidentResponse> getAll() {
        return incidentFacade.getAll();
    }

    @Override
    public List<IncidentResponse> getBySession(Long parkingSessionId) {
        return incidentFacade.getBySession(parkingSessionId);
    }

    @Override
    public List<IncidentResponse> getByStatus(IncidentStatus status) {
        return incidentFacade.getByStatus(status);
    }
}
