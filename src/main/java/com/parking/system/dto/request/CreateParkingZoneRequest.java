package com.parking.system.dto.request;

import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParkingZoneRequest {

    @NotNull
    private Long floorId;

    @NotBlank
    private String zoneCode;

    private String name;

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    @Min(0)
    private Integer capacity;

    private ZoneStatus status;
}
