package com.parking.system.dto.response;

import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VehicleResponse {
    private final Long id;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final String color;
    private final String brand;
    private final Long ownerId;
    private final String ownerUsername;
    private final String ownerFullName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
