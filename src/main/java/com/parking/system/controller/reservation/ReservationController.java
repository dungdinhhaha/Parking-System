package com.parking.system.controller.reservation;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.ReservationAvailabilityResponse;
import com.parking.system.dto.response.ReservationResponse;
import com.parking.system.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DRIVER','MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ReservationResponse> create(Authentication authentication, @Valid @RequestBody CreateReservationRequest request) {
        return ApiResponse.success(201, "Reservation created successfully", reservationService.create(authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DRIVER','MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ReservationResponse> cancel(Authentication authentication, @PathVariable Long id) {
        return ApiResponse.success("Reservation cancelled successfully", reservationService.cancel(authentication.getName(), id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DRIVER','MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ReservationResponse> get(Authentication authentication, @PathVariable Long id) {
        return ApiResponse.success(reservationService.get(authentication.getName(), id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('DRIVER','MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<List<ReservationResponse>> myReservations(Authentication authentication) {
        return ApiResponse.success(reservationService.getMyReservations(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<List<ReservationResponse>> getAll() {
        return ApiResponse.success(reservationService.getAll());
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAnyRole('DRIVER','MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<ReservationAvailabilityResponse> getAvailability(@RequestParam String buildingCode) {
        return ApiResponse.success(reservationService.getAvailability(buildingCode));
    }
}
// Edited by Codex
