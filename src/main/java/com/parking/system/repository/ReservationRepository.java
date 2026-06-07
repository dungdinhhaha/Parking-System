package com.parking.system.repository;

import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCodeIgnoreCase(String reservationCode);
    List<Reservation> findAllByUser_UsernameOrderByCreatedAtDesc(String username);
    boolean existsByAssignedSlot_Id(Long slotId);
    Optional<Reservation> findByAssignedSlot_Id(Long slotId);
    Optional<Reservation> findFirstByPlateNumberIgnoreCaseAndVehicleTypeAndStatusAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByCreatedAtDesc(
            String plateNumber,
            VehicleType vehicleType,
            ReservationStatus status,
            LocalDateTime allocationTimeForStart,
            LocalDateTime allocationTimeForEnd);
}
