package com.parking.system.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildingOccupancyResponse {
    private final Long buildingId;
    private final String buildingCode;
    private final String buildingName;
    private final long totalFloors;
    private final long totalZones;
    private final long totalSlots;
    private final long availableCarSlots;
    private final long occupiedCarSlots;
    private final long reservedCarSlots;
    private final long motorbikeCapacity;
    private final long motorbikeCurrentCount;
    private final long motorbikeReservedCount;
    private final long totalCapacity;
    private final long usedCapacity;
    private final long availableCapacity;
    private final BigDecimal occupancyRate;
}
