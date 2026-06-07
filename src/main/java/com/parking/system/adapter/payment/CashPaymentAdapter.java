package com.parking.system.adapter.payment;

import com.parking.system.enums.PaymentMethod;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentAdapter implements PaymentGatewayAdapter {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.CASH;
    }

    @Override
    public PaymentGatewayResult process(PaymentGatewayRequest request) {
        return PaymentGatewayResult.builder()
                .success(true)
                .provider("CASH")
                .transactionCode("CASH-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT))
                .message("Cash payment completed successfully")
                .build();
    }
}
