package com.parking.system.dto.response;

import com.parking.system.enums.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeeCalculationResponse {
    private final Long parkingSessionId;
    private final String ticketCode;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final LocalDateTime calculationTime;
    private final BigDecimal subtotal;
    private final BigDecimal totalAmount;
    private final List<FeeItemResponse> items;
}
