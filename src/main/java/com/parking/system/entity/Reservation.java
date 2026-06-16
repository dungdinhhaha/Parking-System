package com.parking.system.entity;

import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reservation extends BaseEntity {
    private String reservationCode;
    private String plateNumber;
    private VehicleType vehicleType;
    private ReservationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ParkingZone assignedZone;
    private ParkingSlot assignedSlot;
    private ParkingSession parkingSession;

    public void markUsed() {
        status = ReservationStatus.USED;
    }

    public void cancel() {
        status = ReservationStatus.CANCELLED;
    }

    public boolean isActiveAt(LocalDateTime time) {
        return status == ReservationStatus.CONFIRMED
                && time != null
                && (startTime == null || !time.isBefore(startTime))
                && (endTime == null || !time.isAfter(endTime));
    }
}
