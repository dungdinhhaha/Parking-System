package com.parking.system.policy.fee.model;

import com.parking.system.enums.PricingPolicyType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeeItem {
    private final String code;
    private final String label;
    private final PricingPolicyType policyType;
    private final BigDecimal amount;
    private final String reason;
}
