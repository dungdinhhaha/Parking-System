package com.parking.system.dto.response;

import com.parking.system.enums.PolicyStatus;
import com.parking.system.enums.PricingPolicyType;
import com.parking.system.enums.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PricingPolicyResponse {
    private final Long id;
    private final String name;
    private final VehicleType vehicleType;
    private final PricingPolicyType policyType;
    private final BigDecimal baseFee;
    private final BigDecimal hourlyRate;
    private final BigDecimal lostTicketFee;
    private final BigDecimal overnightFee;
    private final BigDecimal wrongZonePenaltyFee;
    private final LocalDateTime effectiveFrom;
    private final LocalDateTime effectiveTo;
    private final PolicyStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
