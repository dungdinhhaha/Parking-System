package com.parking.system.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingFloorResponse {
    private Long id;
    private Long buildingId;
    private String buildingName;
    private String floorCode;
    private String name;
    private String description;
    private Long zoneCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
