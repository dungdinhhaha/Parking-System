package com.parking.system.service.impl;

import com.parking.system.dto.request.CreatePricingPolicyRequest;
import com.parking.system.dto.request.UpdatePricingPolicyRequest;
import com.parking.system.dto.response.PricingPolicyResponse;
import com.parking.system.entity.PricingPolicy;
import com.parking.system.enums.PolicyStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.PricingPolicyRepository;
import com.parking.system.service.PricingPolicyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PricingPolicyServiceImpl implements PricingPolicyService {

    private final PricingPolicyRepository pricingPolicyRepository;

    @Override
    @Transactional
    public PricingPolicyResponse create(CreatePricingPolicyRequest request) {
        PricingPolicy policy = new PricingPolicy();
        apply(policy, request.getName(), request.getVehicleType(), request.getPolicyType(),
                request.getBaseFee(), request.getHourlyRate(), request.getLostTicketFee(),
                request.getOvernightFee(), request.getWrongZonePenaltyFee(),
                request.getEffectiveFrom(), request.getEffectiveTo(), PolicyStatus.ACTIVE);
        return map(pricingPolicyRepository.save(policy));
    }

    @Override
    @Transactional
    public PricingPolicyResponse update(Long id, UpdatePricingPolicyRequest request) {
        PricingPolicy policy = pricingPolicyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pricing policy not found"));
        apply(policy, request.getName(), request.getVehicleType(), request.getPolicyType(),
                request.getBaseFee(), request.getHourlyRate(), request.getLostTicketFee(),
                request.getOvernightFee(), request.getWrongZonePenaltyFee(),
                request.getEffectiveFrom(), request.getEffectiveTo(), request.getStatus());
        return map(pricingPolicyRepository.save(policy));
    }

    @Override
    public PricingPolicyResponse get(Long id) {
        return map(pricingPolicyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pricing policy not found")));
    }

    @Override
    public List<PricingPolicyResponse> getAll() {
        return pricingPolicyRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    public PricingPolicyResponse getActivePolicy(VehicleType vehicleType) {
        return pricingPolicyRepository.findAllByVehicleTypeAndStatusOrderByCreatedAtDesc(vehicleType, PolicyStatus.ACTIVE)
                .stream()
                .filter(this::isCurrentlyEffective)
                .findFirst()
                .map(this::map)
                .orElseThrow(() -> new BusinessException("Active pricing policy not found for vehicle type " + vehicleType));
    }

    private boolean isCurrentlyEffective(PricingPolicy policy) {
        var now = java.time.LocalDateTime.now();
        boolean fromOk = policy.getEffectiveFrom() == null || !now.isBefore(policy.getEffectiveFrom());
        boolean toOk = policy.getEffectiveTo() == null || !now.isAfter(policy.getEffectiveTo());
        return fromOk && toOk;
    }

    private void apply(PricingPolicy policy,
                       String name,
                       VehicleType vehicleType,
                       com.parking.system.enums.PricingPolicyType policyType,
                       java.math.BigDecimal baseFee,
                       java.math.BigDecimal hourlyRate,
                       java.math.BigDecimal lostTicketFee,
                       java.math.BigDecimal overnightFee,
                       java.math.BigDecimal wrongZonePenaltyFee,
                       java.time.LocalDateTime effectiveFrom,
                       java.time.LocalDateTime effectiveTo,
                       PolicyStatus status) {
        policy.setName(name);
        policy.setVehicleType(vehicleType);
        policy.setPolicyType(policyType);
        policy.setBaseFee(baseFee);
        policy.setHourlyRate(hourlyRate);
        policy.setLostTicketFee(lostTicketFee);
        policy.setOvernightFee(overnightFee);
        policy.setWrongZonePenaltyFee(wrongZonePenaltyFee);
        policy.setEffectiveFrom(effectiveFrom);
        policy.setEffectiveTo(effectiveTo);
        policy.setStatus(status);
    }

    private PricingPolicyResponse map(PricingPolicy policy) {
        return PricingPolicyResponse.builder()
                .id(policy.getId())
                .name(policy.getName())
                .vehicleType(policy.getVehicleType())
                .policyType(policy.getPolicyType())
                .baseFee(policy.getBaseFee())
                .hourlyRate(policy.getHourlyRate())
                .lostTicketFee(policy.getLostTicketFee())
                .overnightFee(policy.getOvernightFee())
                .wrongZonePenaltyFee(policy.getWrongZonePenaltyFee())
                .effectiveFrom(policy.getEffectiveFrom())
                .effectiveTo(policy.getEffectiveTo())
                .status(policy.getStatus())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}
