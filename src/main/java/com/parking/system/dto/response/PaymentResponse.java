package com.parking.system.dto.response;

import com.parking.system.enums.PaymentMethod;
import com.parking.system.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private final Long id;
    private final Long parkingSessionId;
    private final String ticketCode;
    private final String plateNumber;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final String transactionCode;
    private final LocalDateTime paidAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
