package com.parking.system.entity;

import com.parking.system.enums.PolicyStatus;
import com.parking.system.enums.PricingPolicyType;
import com.parking.system.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pricing_policies")
@Getter
@Setter
public class PricingPolicy extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PricingPolicyType policyType;

    @Column(precision = 19, scale = 2)
    private BigDecimal baseFee;

    @Column(precision = 19, scale = 2)
    private BigDecimal hourlyRate;

    @Column(precision = 19, scale = 2)
    private BigDecimal lostTicketFee;

    @Column(precision = 19, scale = 2)
    private BigDecimal overnightFee;

    @Column(precision = 19, scale = 2)
    private BigDecimal wrongZonePenaltyFee;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PolicyStatus status;
}
