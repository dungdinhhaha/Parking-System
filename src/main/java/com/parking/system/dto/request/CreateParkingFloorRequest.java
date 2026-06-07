package com.parking.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParkingFloorRequest {

    @NotNull
    private Long buildingId;

    @NotBlank
    private String floorCode;

    private String name;
    private String description;
}
