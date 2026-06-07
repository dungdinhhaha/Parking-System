package com.parking.system.service;

import com.parking.system.dto.request.CreatePaymentRequest;
import com.parking.system.dto.response.PaymentResponse;
import java.util.List;

public interface PaymentService {
    PaymentResponse create(CreatePaymentRequest request);

    PaymentResponse get(Long id);

    PaymentResponse getByParkingSessionId(Long parkingSessionId);

    List<PaymentResponse> getAll();
}
