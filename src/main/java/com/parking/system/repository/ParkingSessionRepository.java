package com.parking.system.repository;

import com.parking.system.entity.ParkingSession;
import com.parking.system.enums.SessionStatus;
import java.util.Optional;

public interface ParkingSessionRepository {
    Optional<ParkingSession> findByCheckInRequestId(String requestId);
    Optional<ParkingSession> findByCheckOutRequestId(String requestId);
    Optional<ParkingSession> findDetailedFirstByRfidCardIdIgnoreCaseAndStatusOrderByCreatedAtDesc(String rfidCardId, SessionStatus status);
    Optional<ParkingSession> findDetailedById(Long id);
    Optional<ParkingSession> findDetailedByIdForUpdate(Long id);
    boolean existsByPlateNumberIgnoreCaseAndStatus(String plateNumber, SessionStatus status);
    boolean existsByRfidCardIdIgnoreCaseAndStatus(String rfidCardId, SessionStatus status);
    ParkingSession save(ParkingSession session);
}
