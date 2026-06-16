package com.parking.system.strategy.allocation;

import com.parking.system.enums.VehicleType;

public interface ParkingAllocationStrategy {
    VehicleType supports();
    AllocationResult allocate(AllocationRequest request);
}
