package com.parking.system.policy.fee.registry;

import com.parking.system.policy.fee.FeePolicy;
import com.parking.system.policy.fee.context.FeeCalculationContext;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FeePolicyRegistry {

    private final List<FeePolicy> feePolicies;

    public FeePolicyRegistry(List<FeePolicy> feePolicies) {
        this.feePolicies = feePolicies;
    }

    public List<FeePolicy> resolve(FeeCalculationContext context) {
        return feePolicies.stream()
                .filter(Objects::nonNull)
                .filter(policy -> policy.applies(context))
                .sorted(Comparator.comparingInt(FeePolicy::order))
                .toList();
    }
}
