package com.parking.system.dto.request;

import com.parking.system.enums.BuildingStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParkingBuildingRequest {

    @NotBlank
    private String buildingCode;

    @NotBlank
    private String name;

    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
    private BuildingStatus status;
    private String description;
}
