package com.parking.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateParkingFloorRequest {

    @NotBlank
    private String floorCode;

    private String name;
    private String description;
}
