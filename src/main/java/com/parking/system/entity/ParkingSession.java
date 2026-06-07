package com.parking.system.entity;

import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "parking_sessions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_sessions_ticket_code", columnNames = "ticket_code")
        }
)
@Getter
@Setter
public class ParkingSession extends BaseEntity {

    @Column(name = "ticket_code", nullable = false, length = 50)
    private String ticketCode;

    @Column(nullable = false, length = 30)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleType vehicleType;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Column(length = 100)
    private String entryGate;

    @Column(length = 100)
    private String exitGate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SessionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_zone_id", nullable = false)
    private ParkingZone assignedZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_slot_id")
    private ParkingSlot assignedSlot;

    @OneToOne(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private List<ParkingIncident> incidents = new ArrayList<>();

    @OneToMany(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private List<PlateRecognitionLog> recognitionLogs = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    @OneToMany(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    public void complete() {
        status = SessionStatus.COMPLETED;
    }

    public void markUnpaid() {
        status = SessionStatus.UNPAID;
    }

    public void markWrongPlate() {
        status = SessionStatus.WRONG_PLATE;
    }

    public void markLostTicket() {
        status = SessionStatus.LOST_TICKET;
    }

    public void markWrongZone() {
        status = SessionStatus.WRONG_ZONE;
    }

    public void cancel() {
        status = SessionStatus.CANCELLED;
    }
}
