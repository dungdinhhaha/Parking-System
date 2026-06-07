package com.parking.system.dto.request;

import com.parking.system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleRequest {
    @NotBlank
    private String plateNumber;

    @NotNull
    private VehicleType vehicleType;

    private String color;
    private String brand;

    private Long ownerId;
}
