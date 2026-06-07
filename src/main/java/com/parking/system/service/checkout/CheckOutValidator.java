package com.parking.system.service.checkout;

import com.parking.system.dto.request.CheckOutRequest;
import com.parking.system.entity.ParkingSession;
import com.parking.system.enums.SessionStatus;
import com.parking.system.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class CheckOutValidator {

    public void validate(ParkingSession session, CheckOutRequest request) {
        if (session == null) {
            throw new BusinessException("Parking session not found");
        }
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new BusinessException("Parking session is not active");
        }
        if (request.getPlateNumber() != null && !request.getPlateNumber().isBlank()) {
            String expected = normalize(session.getPlateNumber());
            String actual = normalize(request.getPlateNumber());
            if (!expected.equals(actual)) {
                throw new BusinessException("Plate number does not match active session");
            }
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
