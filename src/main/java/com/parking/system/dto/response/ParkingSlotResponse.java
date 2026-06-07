package com.parking.system.dto.response;

import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSlotResponse {
    private Long id;
    private Long zoneId;
    private String zoneCode;
    private String slotCode;
    private VehicleType vehicleType;
    private SlotStatus status;
    private Double distanceFromGate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
