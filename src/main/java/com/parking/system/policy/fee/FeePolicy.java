package com.parking.system.policy.fee;

import com.parking.system.enums.PricingPolicyType;
import com.parking.system.policy.fee.context.FeeCalculationContext;
import com.parking.system.policy.fee.model.FeeItem;

public interface FeePolicy {
    PricingPolicyType supports();

    int order();

    boolean applies(FeeCalculationContext context);

    FeeItem calculate(FeeCalculationContext context);
}
