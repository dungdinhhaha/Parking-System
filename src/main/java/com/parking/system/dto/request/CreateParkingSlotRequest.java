package com.parking.system.dto.request;

import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParkingSlotRequest {

    @NotNull
    private Long zoneId;

    @NotBlank
    private String slotCode;

    @NotNull
    private VehicleType vehicleType;

    private Double distanceFromGate;
    private SlotStatus status;
}
