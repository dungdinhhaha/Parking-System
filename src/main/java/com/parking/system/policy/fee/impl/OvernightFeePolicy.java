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
public class OvernightFeePolicy implements FeePolicy {

    @Override
    public PricingPolicyType supports() {
        return PricingPolicyType.OVERNIGHT;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean applies(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        return policy.getOvernightFee() != null && context.getOvernightCount() > 0;
    }

    @Override
    public FeeItem calculate(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        BigDecimal overnightFee = policy.getOvernightFee() == null ? BigDecimal.ZERO : policy.getOvernightFee();
        BigDecimal nights = BigDecimal.valueOf(context.getOvernightCount());
        BigDecimal amount = overnightFee.multiply(nights).setScale(2, RoundingMode.HALF_UP);

        return FeeItem.builder()
                .code("OVERNIGHT_FEE")
                .label("Overnight fee")
                .policyType(supports())
                .amount(amount)
                .reason("Applied for each overnight crossing")
                .build();
    }
}
