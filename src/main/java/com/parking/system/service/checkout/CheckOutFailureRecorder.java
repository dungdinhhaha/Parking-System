package com.parking.system.service.checkout;

import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingIncident;
import com.parking.system.entity.User;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.enums.IncidentType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingIncidentRepository;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.operations.OperationsRealtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CheckOutFailureRecorder {

    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingIncidentRepository parkingIncidentRepository;
    private final UserRepository userRepository;
    private final OperationsRealtimeService operationsRealtimeService;

    @Transactional
    public void recordWrongPlate(Long sessionId, String username, String detectedPlate) {
        ParkingSession session = parkingSessionRepository.findDetailedByIdForUpdate(sessionId)
                .orElseThrow(() -> new BusinessException("Parking session not found"));
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        ParkingIncident incident = new ParkingIncident();
        incident.setIncidentType(IncidentType.WRONG_PLATE);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setParkingSession(session);
        incident.setReportedBy(staff);
        incident.setDescription("Expected plate " + session.getPlateNumber()
                + " but detected " + detectedPlate);
        ParkingIncident saved = parkingIncidentRepository.save(incident);
        String buildingCode = session.getAssignedZone() != null
                && session.getAssignedZone().getFloor() != null
                && session.getAssignedZone().getFloor().getBuilding() != null
                ? session.getAssignedZone().getFloor().getBuilding().getBuildingCode()
                : null;
        operationsRealtimeService.publishWarning(
                "CHECK_OUT",
                "Sai biển số khi ra",
                "Biển số camera không khớp với session đang ACTIVE",
                buildingCode,
                saved.getId());
    }
}
