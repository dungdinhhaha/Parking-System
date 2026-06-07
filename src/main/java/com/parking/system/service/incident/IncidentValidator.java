package com.parking.system.service.incident;

import com.parking.system.dto.request.CreateIncidentRequest;
import com.parking.system.dto.request.ResolveIncidentRequest;
import com.parking.system.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class IncidentValidator {

    public void validateCreateRequest(CreateIncidentRequest request) {
        if (request == null) {
            throw new BusinessException("Incident request is required");
        }
        if (request.getParkingSessionId() == null) {
            throw new BusinessException("Parking session is required");
        }
        if (request.getIncidentType() == null) {
            throw new BusinessException("Incident type is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new BusinessException("Incident description is required");
        }
    }

    public void validateCloseRequest(ResolveIncidentRequest request) {
        if (request == null) {
            throw new BusinessException("Resolution request is required");
        }
        if (request.getResolutionNote() == null || request.getResolutionNote().isBlank()) {
            throw new BusinessException("Resolution note is required");
        }
    }
}
