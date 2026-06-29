package com.parking.system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenewParkingSubscriptionRequest {

    @NotNull
    @Min(1)
    private Integer extendDays;

    private String notes;
}
