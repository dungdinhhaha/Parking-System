package com.parking.system.strategy.allocation;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllocationResult {
    private final ParkingZone assignedZone;
    private final ParkingSlot assignedSlot;
    private final Reservation matchedReservation;
    private final AllocationSource source;
}
