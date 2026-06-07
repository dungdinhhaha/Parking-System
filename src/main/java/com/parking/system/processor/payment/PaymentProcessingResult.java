package com.parking.system.processor.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentProcessingResult {
    private final boolean success;
    private final String provider;
    private final String transactionCode;
    private final String message;
}
