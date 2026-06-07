package com.parking.system.config;

import com.parking.system.entity.PricingPolicy;
import com.parking.system.enums.PolicyStatus;
import com.parking.system.enums.PricingPolicyType;
import com.parking.system.enums.VehicleType;
import com.parking.system.repository.PricingPolicyRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Configuration
public class PricingSeedConfig {

    @Bean
    @Profile("!test")
    @Order(6)
    public CommandLineRunner seedPricingPolicies(PricingPolicyRepository pricingPolicyRepository) {
        return args -> {
            seedPolicy(
                    pricingPolicyRepository,
                    "CAR STANDARD POLICY",
                    VehicleType.CAR,
                    PricingPolicyType.HOURLY,
                    new BigDecimal("5000"),
                    new BigDecimal("3000"),
                    new BigDecimal("50000"),
                    new BigDecimal("20000"),
                    new BigDecimal("15000"));

            seedPolicy(
                    pricingPolicyRepository,
                    "MOTORBIKE STANDARD POLICY",
                    VehicleType.MOTORBIKE,
                    PricingPolicyType.HOURLY,
                    new BigDecimal("2000"),
                    new BigDecimal("1000"),
                    new BigDecimal("20000"),
                    new BigDecimal("10000"),
                    new BigDecimal("8000"));
        };
    }

    private void seedPolicy(PricingPolicyRepository pricingPolicyRepository,
                            String name,
                            VehicleType vehicleType,
                            PricingPolicyType policyType,
                            BigDecimal baseFee,
                            BigDecimal hourlyRate,
                            BigDecimal lostTicketFee,
                            BigDecimal overnightFee,
                            BigDecimal wrongZonePenaltyFee) {
        if (!pricingPolicyRepository.findAllByVehicleTypeAndStatusOrderByCreatedAtDesc(vehicleType, PolicyStatus.ACTIVE).isEmpty()) {
            return;
        }

        PricingPolicy policy = new PricingPolicy();
        policy.setName(name);
        policy.setVehicleType(vehicleType);
        policy.setPolicyType(policyType);
        policy.setBaseFee(baseFee);
        policy.setHourlyRate(hourlyRate);
        policy.setLostTicketFee(lostTicketFee);
        policy.setOvernightFee(overnightFee);
        policy.setWrongZonePenaltyFee(wrongZonePenaltyFee);
        policy.setEffectiveFrom(LocalDateTime.now().minusDays(1));
        policy.setStatus(PolicyStatus.ACTIVE);
        pricingPolicyRepository.save(policy);
    }
}
