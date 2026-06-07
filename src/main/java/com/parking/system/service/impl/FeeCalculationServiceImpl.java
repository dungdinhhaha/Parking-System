package com.parking.system.service.impl;

import com.parking.system.dto.request.CalculateFeeRequest;
import com.parking.system.dto.response.FeeCalculationResponse;
import com.parking.system.dto.response.FeeItemResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PricingPolicy;
import com.parking.system.exception.BusinessException;
import com.parking.system.policy.fee.FeePolicy;
import com.parking.system.policy.fee.context.FeeCalculationContext;
import com.parking.system.policy.fee.model.FeeCalculationResult;
import com.parking.system.policy.fee.model.FeeItem;
import com.parking.system.policy.fee.registry.FeePolicyRegistry;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PricingPolicyRepository;
import com.parking.system.enums.PolicyStatus;
import com.parking.system.service.FeeCalculationService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeeCalculationServiceImpl implements FeeCalculationService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final PricingPolicyRepository pricingPolicyRepository;
    private final FeePolicyRegistry feePolicyRegistry;

    @Override
    public FeeCalculationResponse calculate(CalculateFeeRequest request) {
        ParkingSession session = parkingSessionRepository.findById(request.getParkingSessionId())
                .orElseThrow(() -> new BusinessException("Parking session not found"));
        PricingPolicy pricingPolicy = pricingPolicyRepository
                .findAllByVehicleTypeAndStatusOrderByCreatedAtDesc(session.getVehicleType(), PolicyStatus.ACTIVE)
                .stream()
                .filter(policy -> {
                    LocalDateTime now = request.getCalculationTime() != null ? request.getCalculationTime() : LocalDateTime.now();
                    boolean fromOk = policy.getEffectiveFrom() == null || !now.isBefore(policy.getEffectiveFrom());
                    boolean toOk = policy.getEffectiveTo() == null || !now.isAfter(policy.getEffectiveTo());
                    return fromOk && toOk;
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException("Active pricing policy not found for vehicle type " + session.getVehicleType()));

        LocalDateTime calculationTime = request.getCalculationTime() != null
                ? request.getCalculationTime()
                : LocalDateTime.now();

        FeeCalculationContext context = FeeCalculationContext.builder()
                .parkingSession(session)
                .pricingPolicy(pricingPolicy)
                .calculationTime(calculationTime)
                .lostTicket(Boolean.TRUE.equals(request.getLostTicket()))
                .wrongZone(Boolean.TRUE.equals(request.getWrongZone()))
                .build();

        List<FeeItem> items = feePolicyRegistry.resolve(context).stream()
                .map(policy -> policy.calculate(context))
                .toList();

        BigDecimal subtotal = items.stream()
                .map(FeeItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = subtotal;

        FeeCalculationResult result = FeeCalculationResult.builder()
                .parkingSessionId(session.getId())
                .ticketCode(session.getTicketCode())
                .plateNumber(session.getPlateNumber())
                .vehicleType(session.getVehicleType().name())
                .checkInTime(session.getCheckInTime())
                .checkOutTime(session.getCheckOutTime())
                .calculationTime(calculationTime)
                .subtotal(subtotal)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        return map(result);
    }

    private FeeCalculationResponse map(FeeCalculationResult result) {
        return FeeCalculationResponse.builder()
                .parkingSessionId(result.getParkingSessionId())
                .ticketCode(result.getTicketCode())
                .plateNumber(result.getPlateNumber())
                .vehicleType(com.parking.system.enums.VehicleType.valueOf(result.getVehicleType()))
                .checkInTime(result.getCheckInTime())
                .checkOutTime(result.getCheckOutTime())
                .calculationTime(result.getCalculationTime())
                .subtotal(result.getSubtotal())
                .totalAmount(result.getTotalAmount())
                .items(result.getItems().stream().map(this::map).toList())
                .build();
    }

    private FeeItemResponse map(FeeItem item) {
        return FeeItemResponse.builder()
                .code(item.getCode())
                .label(item.getLabel())
                .policyType(item.getPolicyType())
                .amount(item.getAmount())
                .reason(item.getReason())
                .build();
    }
}
