package com.parking.system.adapter.payment;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentGatewayRequest {
    private final Long paymentId;
    private final Long parkingSessionId;
    private final String ticketCode;
    private final String plateNumber;
    private final BigDecimal amount;
    private final String note;
}
