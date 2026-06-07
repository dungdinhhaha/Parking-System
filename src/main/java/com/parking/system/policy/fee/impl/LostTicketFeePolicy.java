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
public class LostTicketFeePolicy implements FeePolicy {

    @Override
    public PricingPolicyType supports() {
        return PricingPolicyType.LOST_TICKET;
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public boolean applies(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        return policy.getLostTicketFee() != null && context.hasLostTicketCondition();
    }

    @Override
    public FeeItem calculate(FeeCalculationContext context) {
        PricingPolicy policy = context.getPricingPolicy();
        BigDecimal amount = policy.getLostTicketFee().setScale(2, RoundingMode.HALF_UP);

        return FeeItem.builder()
                .code("LOST_TICKET_FEE")
                .label("Lost ticket fee")
                .policyType(supports())
                .amount(amount)
                .reason("Applied when session is marked lost ticket")
                .build();
    }
}
