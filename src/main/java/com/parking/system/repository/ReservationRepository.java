package com.parking.system.repository;

import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository {
    List<Reservation> findMatchingForUpdate(String plateNumber, VehicleType vehicleType, ReservationStatus status, LocalDateTime atTime);
}
