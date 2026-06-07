package com.parking.system.dto.response;

import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingZoneResponse {
    private Long id;
    private Long floorId;
    private String floorCode;
    private String zoneCode;
    private String name;
    private VehicleType vehicleType;
    private Integer capacity;
    private Integer currentCount;
    private Integer reservedCount;
    private Integer availableCapacity;
    private ZoneStatus status;
    private Long slotCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
