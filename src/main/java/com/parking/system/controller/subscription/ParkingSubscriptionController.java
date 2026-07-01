package com.parking.system.controller.subscription;

import com.parking.system.dto.request.CreateParkingSubscriptionRequest;
import com.parking.system.dto.request.RenewParkingSubscriptionRequest;
import com.parking.system.dto.request.UpdateParkingSubscriptionRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.ParkingSubscriptionHistoryResponse;
import com.parking.system.dto.response.ParkingSubscriptionResponse;
import com.parking.system.service.ParkingSubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class ParkingSubscriptionController {

    private final ParkingSubscriptionService parkingSubscriptionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ParkingSubscriptionResponse> create(Authentication authentication,
                                                          @Valid @RequestBody CreateParkingSubscriptionRequest request) {
        return ApiResponse.success(201, "Subscription created successfully",
                parkingSubscriptionService.create(authentication.getName(), request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<List<ParkingSubscriptionResponse>> getAll() {
        return ApiResponse.success(parkingSubscriptionService.getAll());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ApiResponse<List<ParkingSubscriptionResponse>> getMine(Authentication authentication) {
        return ApiResponse.success(parkingSubscriptionService.getMine(authentication.getName()));
    }

    @GetMapping("/by-plate")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<List<ParkingSubscriptionResponse>> findAllByPlateNumber(
            @RequestParam String plateNumber) {
        return ApiResponse.success(parkingSubscriptionService.findAllByPlateNumber(plateNumber));
    }

    @GetMapping("/by-plate/{plateNumber}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ParkingSubscriptionResponse> findByPlateNumber(@PathVariable String plateNumber) {
        return ApiResponse.success(parkingSubscriptionService.findByPlateNumber(plateNumber));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ParkingSubscriptionResponse> get(@PathVariable Long id) {
        return ApiResponse.success(parkingSubscriptionService.get(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ParkingSubscriptionResponse> cancel(Authentication authentication,
                                                           @PathVariable Long id) {
        return ApiResponse.success("Subscription cancelled successfully",
                parkingSubscriptionService.cancel(authentication.getName(), id));
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ParkingSubscriptionResponse> renew(Authentication authentication,
                                                          @PathVariable Long id,
                                                          @Valid @RequestBody RenewParkingSubscriptionRequest request) {
        return ApiResponse.success("Subscription renewed successfully",
                parkingSubscriptionService.renew(authentication.getName(), id, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ParkingSubscriptionResponse> update(Authentication authentication,
                                                           @PathVariable Long id,
                                                           @Valid @RequestBody UpdateParkingSubscriptionRequest request) {
        return ApiResponse.success("Subscription updated successfully",
                parkingSubscriptionService.update(authentication.getName(), id, request));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<List<ParkingSubscriptionHistoryResponse>> getHistory(@PathVariable Long id) {
        return ApiResponse.success(parkingSubscriptionService.getHistory(id));
    }
}
