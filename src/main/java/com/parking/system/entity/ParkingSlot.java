package com.parking.system.entity;

import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSlot extends BaseEntity {
    private String slotCode;
    private VehicleType vehicleType;
    private SlotStatus status;
    private Double distanceFromGate;
    private ParkingZone zone;

    public void occupy() {
        status = SlotStatus.OCCUPIED;
    }

    public void occupyReserved() {
        status = SlotStatus.OCCUPIED;
    }

    public void release() {
        status = SlotStatus.AVAILABLE;
    }

    public void reserve() {
        status = SlotStatus.RESERVED;
    }

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == SlotStatus.RESERVED;
    }
}
