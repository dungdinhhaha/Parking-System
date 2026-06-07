package com.parking.system.policy.fee.impl;

import com.parking.system.entity.PricingPolicy;
import com.parking.system.enums.PricingPolicyType;
import com.parking.system.policy.fee.FeePolicy;
import com.parking.system.policy.fee.context.FeeCalculationContext;
import com.parking.system.policy.fee.model.FeeItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class WrongZonePenaltyPolicy implements FeePolicy {

    @Override
    public PricingPolicyType supports() {
        return PricingPolicyType.WRONG_ZONE_PENALTY;
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public boolean applies(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        return policy.getWrongZonePenaltyFee() != null && context.hasWrongZoneCondition();
    }

    @Override
    public FeeItem calculate(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        BigDecimal amount = policy.getWrongZonePenaltyFee().setScale(2, RoundingMode.HALF_UP);

        return FeeItem.builder()
                .code("WRONG_ZONE_PENALTY")
                .label("Wrong zone penalty")
                .policyType(supports())
                .amount(amount)
                .reason("Applied when session is marked wrong zone")
                .build();
    }
}
