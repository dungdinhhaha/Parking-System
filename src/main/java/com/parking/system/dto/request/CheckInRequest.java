package com.parking.system.dto.request;

import com.parking.system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckInRequest {
    @NotBlank
    private String buildingCode;

    @NotNull
    private VehicleType vehicleType;

    @NotBlank
    private String plateNumber;

    private String entryGate;
}
