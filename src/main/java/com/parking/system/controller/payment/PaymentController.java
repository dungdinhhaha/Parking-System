package com.parking.system.controller.payment;

import com.parking.system.dto.request.CreatePaymentRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.PaymentResponse;
import com.parking.system.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('DRIVER','STAFF','MANAGER','SYSTEM_ADMIN')")
    @PostMapping
    public ApiResponse<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.success(201, "Payment processed successfully", paymentService.create(request));
    }

    @PreAuthorize("hasAnyRole('DRIVER','STAFF','MANAGER','SYSTEM_ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> get(@PathVariable Long id) {
        return ApiResponse.success(paymentService.get(id));
    }

    @PreAuthorize("hasAnyRole('DRIVER','STAFF','MANAGER','SYSTEM_ADMIN')")
    @GetMapping("/session/{parkingSessionId}")
    public ApiResponse<PaymentResponse> getByParkingSession(@PathVariable Long parkingSessionId) {
        return ApiResponse.success(paymentService.getByParkingSessionId(parkingSessionId));
    }

    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    @GetMapping
    public ApiResponse<List<PaymentResponse>> getAll() {
        return ApiResponse.success(paymentService.getAll());
    }
}
