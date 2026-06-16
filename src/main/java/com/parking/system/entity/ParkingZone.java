package com.parking.system.entity;

import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingZone extends BaseEntity {
    private String zoneCode;
    private VehicleType vehicleType;
    private Integer capacity = 0;
    private Integer currentCount = 0;
    private Integer reservedCount = 0;
    private ZoneStatus status;
    private ParkingFloor floor;
    private List<ParkingSlot> slots = new ArrayList<>();

    public void increaseCurrentCount() {
        currentCount = safe(currentCount) + 1;
    }

    public void decreaseCurrentCount() {
        currentCount = Math.max(0, safe(currentCount) - 1);
    }

    public void increaseReservedCount() {
        reservedCount = safe(reservedCount) + 1;
    }

    public void decreaseReservedCount() {
        reservedCount = Math.max(0, safe(reservedCount) - 1);
    }

    public int getAvailableCapacity() {
        return Math.max(0, safe(capacity) - safe(currentCount) - safe(reservedCount));
    }

    public boolean hasAvailableCapacity() {
        return getAvailableCapacity() > 0;
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}
