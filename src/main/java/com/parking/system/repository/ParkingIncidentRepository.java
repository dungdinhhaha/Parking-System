package com.parking.system.repository;

import com.parking.system.entity.ParkingIncident;
import com.parking.system.enums.IncidentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingIncidentRepository extends JpaRepository<ParkingIncident, Long> {
    List<ParkingIncident> findAllByParkingSession_IdOrderByCreatedAtDesc(Long parkingSessionId);
    List<ParkingIncident> findAllByStatusOrderByCreatedAtDesc(IncidentStatus status);
    List<ParkingIncident> findAllByOrderByCreatedAtDesc();
}
