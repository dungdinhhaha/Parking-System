package com.parking.system.service.reservation;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ReservationValidator {

    public void validateCreateRequest(CreateReservationRequest request) {
        if (request == null) {
            throw new BusinessException("Reservation request is required");
        }
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateVehicleSpecificFields(request);
    }

    public void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BusinessException("Start time and end time are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("Start time must be before end time");
        }
    }

    private void validateVehicleSpecificFields(CreateReservationRequest request) {
        if (request.getVehicleType() == VehicleType.CAR) {
            if (request.getZoneId() == null || request.getSlotId() == null) {
                throw new BusinessException("Car reservation requires zoneId and slotId");
            }
            return;
        }
        if (request.getVehicleType() == VehicleType.MOTORBIKE && request.getZoneId() == null) {
            throw new BusinessException("Motorbike reservation requires zoneId");
        }
    }
}
// Edited by Codex
