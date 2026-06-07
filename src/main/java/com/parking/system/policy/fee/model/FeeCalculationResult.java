package com.parking.system.policy.fee.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeeCalculationResult {
    private final Long parkingSessionId;
    private final String ticketCode;
    private final String plateNumber;
    private final String vehicleType;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final LocalDateTime calculationTime;
    private final BigDecimal subtotal;
    private final BigDecimal totalAmount;
    private final List<FeeItem> items;
}
