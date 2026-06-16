package com.parking.system.dto.request;

public record CreatePaymentRequest(Long parkingSessionId, String paymentMethod) {
}
