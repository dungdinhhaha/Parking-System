package com.parking.system.strategy.allocation;

import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllocationRequest {
    private final String buildingCode;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final LocalDateTime allocationTime;
}
