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
public class HourlyFeePolicy implements FeePolicy {

    @Override
    public PricingPolicyType supports() {
        return PricingPolicyType.HOURLY;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean applies(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        return policy.getBaseFee() != null || policy.getHourlyRate() != null;
    }

    @Override
    public FeeItem calculate(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        BigDecimal baseFee = policy.getBaseFee() == null ? BigDecimal.ZERO : policy.getBaseFee();
        BigDecimal hourlyRate = policy.getHourlyRate() == null ? BigDecimal.ZERO : policy.getHourlyRate();
        BigDecimal hours = BigDecimal.valueOf(context.getBillableHours());
        BigDecimal amount = baseFee.add(hourlyRate.multiply(hours)).setScale(2, RoundingMode.HALF_UP);

        return FeeItem.builder()
                .code("HOURLY_FEE")
                .label("Hourly parking fee")
                .policyType(supports())
                .amount(amount)
                .reason("Base fee + hourly rate x billable hours")
                .build();
    }
}
