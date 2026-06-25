package com.parking.system.repository;

import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select reservation from Reservation reservation
            left join fetch reservation.assignedSlot
            join fetch reservation.assignedZone
            where upper(reservation.plateNumber) = upper(:plateNumber)
              and reservation.vehicleType = :vehicleType
              and reservation.status = :status
              and reservation.startTime <= :allocationTime
              and reservation.endTime >= :allocationTime
            order by reservation.createdAt desc
            """)
    List<Reservation> findMatchingForUpdate(@Param("plateNumber") String plateNumber,
                                            @Param("vehicleType") VehicleType vehicleType,
                                            @Param("status") ReservationStatus status,
                                            @Param("allocationTime") LocalDateTime allocationTime);

    List<Reservation> findAllByStatusAndEndTimeBefore(ReservationStatus status, LocalDateTime endTime);
    List<Reservation> findAllByStatusAndStartTimeBefore(ReservationStatus status, LocalDateTime startTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select reservation from Reservation reservation
            left join fetch reservation.assignedSlot
            join fetch reservation.assignedZone
            where reservation.id = :id
            """)
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);
}
