package com.parking.system.dto.request;

import com.parking.system.enums.PricingPolicyType;
import com.parking.system.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePricingPolicyRequest {
    @NotBlank
    private String name;

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    private PricingPolicyType policyType;

    @NotNull
    private BigDecimal baseFee;

    @NotNull
    private BigDecimal hourlyRate;

    @NotNull
    private BigDecimal lostTicketFee;

    @NotNull
    private BigDecimal overnightFee;

    @NotNull
    private BigDecimal wrongZonePenaltyFee;

    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
}
