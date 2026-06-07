package com.parking.system.repository;

import com.parking.system.entity.ParkingSession;
import com.parking.system.enums.SessionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
    boolean existsByAssignedSlot_Id(Long slotId);
    Optional<ParkingSession> findByAssignedSlot_Id(Long slotId);
    boolean existsByPlateNumberIgnoreCaseAndStatus(String plateNumber, SessionStatus status);
    Optional<ParkingSession> findFirstByPlateNumberIgnoreCaseAndStatusOrderByCreatedAtDesc(String plateNumber, SessionStatus status);
}
