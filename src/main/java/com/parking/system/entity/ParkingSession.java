package com.parking.system.entity;

import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSession extends BaseEntity {
    private String ticketCode;
    private String checkInRequestId;
    private String checkOutRequestId;
    private String plateNumber;
    private String rfidCardId;
    private VehicleType vehicleType;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String entryGate;
    private String exitGate;
    private SessionStatus status;
    private Vehicle vehicle;
    private ParkingZone assignedZone;
    private ParkingSlot assignedSlot;
    private List<ParkingIncident> incidents = new ArrayList<>();
    private List<PlateRecognitionLog> recognitionLogs = new ArrayList<>();
    private Reservation reservation;
    private ParkingSubscription subscription;
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
