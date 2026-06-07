package com.parking.system.processor;

import com.parking.system.enums.PaymentMethod;
import com.parking.system.processor.payment.PaymentProcessingContext;
import com.parking.system.processor.payment.PaymentProcessingResult;

public interface PaymentProcessor {
    PaymentMethod supports();

    PaymentProcessingResult process(PaymentProcessingContext context);
}
