package com.parking.system.dto.request;

import com.parking.system.enums.VehicleType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReservationRequest {

    @NotBlank
    private String plateNumber;

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;

    @NotNull
    @FutureOrPresent
    private LocalDateTime endTime;

    private Long zoneId;
    private Long slotId;
}
