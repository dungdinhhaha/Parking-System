package com.parking.system.dto.response;

import com.parking.system.enums.BuildingStatus;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingBuildingResponse {
    private Long id;
    private String buildingCode;
    private String name;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
    private BuildingStatus status;
    private String description;
    private Long floorCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
