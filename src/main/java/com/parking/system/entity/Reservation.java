package com.parking.system.entity;

import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "reservations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reservations_reservation_code", columnNames = "reservation_code")
        }
)
@Getter
@Setter
public class Reservation extends BaseEntity {

    @Column(name = "reservation_code", nullable = false, length = 50)
    private String reservationCode;

    @Column(nullable = false, length = 30)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleType vehicleType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReservationStatus status;

    private LocalDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_zone_id", nullable = false)
    private ParkingZone assignedZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_slot_id")
    private ParkingSlot assignedSlot;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private ParkingSession parkingSession;

    public void confirm() {
        status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        status = ReservationStatus.CANCELLED;
    }

    public void expire() {
        status = ReservationStatus.EXPIRED;
    }

    public void markUsed() {
        usedAt = LocalDateTime.now();
        status = ReservationStatus.USED;
    }
}
// Edited by Codex
