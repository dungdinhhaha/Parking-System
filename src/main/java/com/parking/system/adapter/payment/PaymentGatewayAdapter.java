package com.parking.system.adapter.payment;

import com.parking.system.enums.PaymentMethod;

public interface PaymentGatewayAdapter {
    PaymentMethod supports();

    PaymentGatewayResult process(PaymentGatewayRequest request);
}
