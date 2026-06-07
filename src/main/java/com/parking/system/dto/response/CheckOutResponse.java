package com.parking.system.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckOutResponse {
    private final Long parkingSessionId;
    private final String ticketCode;
    private final String plateNumber;
    private final String exitGate;
    private final LocalDateTime checkOutTime;
    private final BigDecimal feeAmount;
    private final Long paymentId;
    private final String paymentStatus;
    private final String paymentTransactionCode;
    private final String message;
}
