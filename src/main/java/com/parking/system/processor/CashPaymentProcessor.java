package com.parking.system.processor;

import com.parking.system.adapter.payment.CashPaymentAdapter;
import com.parking.system.adapter.payment.PaymentGatewayRequest;
import com.parking.system.adapter.payment.PaymentGatewayResult;
import com.parking.system.enums.PaymentMethod;
import com.parking.system.processor.payment.PaymentProcessingContext;
import com.parking.system.processor.payment.PaymentProcessingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashPaymentProcessor implements PaymentProcessor {

    private final CashPaymentAdapter cashPaymentAdapter;

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.CASH;
    }

    @Override
    public PaymentProcessingResult process(PaymentProcessingContext context) {
        PaymentGatewayResult gatewayResult = cashPaymentAdapter.process(PaymentGatewayRequest.builder()
                .paymentId(context.getPayment().getId())
                .parkingSessionId(context.getParkingSession().getId())
                .ticketCode(context.getParkingSession().getTicketCode())
                .plateNumber(context.getParkingSession().getPlateNumber())
                .amount(context.getAmount())
                .note("Cash payment for parking session")
                .build());

        return PaymentProcessingResult.builder()
                .success(gatewayResult.isSuccess())
                .provider(gatewayResult.getProvider())
                .transactionCode(gatewayResult.getTransactionCode())
                .message(gatewayResult.getMessage())
                .build();
    }
}
