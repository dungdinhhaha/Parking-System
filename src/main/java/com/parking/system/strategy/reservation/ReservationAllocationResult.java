package com.parking.system.strategy.reservation;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationAllocationResult {
    private final ParkingZone assignedZone;
    private final ParkingSlot assignedSlot;
}
