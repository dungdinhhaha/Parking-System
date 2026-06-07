package com.parking.system.dto.response;

import com.parking.system.enums.PricingPolicyType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeeItemResponse {
    private final String code;
    private final String label;
    private final PricingPolicyType policyType;
    private final BigDecimal amount;
    private final String reason;
}
