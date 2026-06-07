package com.parking.system.controller.vehicle;

import com.parking.system.dto.request.CreateVehicleRequest;
import com.parking.system.dto.request.UpdateVehicleRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.VehicleResponse;
import com.parking.system.service.VehicleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('DRIVER','MANAGER','SYSTEM_ADMIN')")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ApiResponse<VehicleResponse> create(Authentication authentication,
                                                @Valid @RequestBody CreateVehicleRequest request) {
        return ApiResponse.success(201, "Vehicle created successfully",
                vehicleService.create(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VehicleResponse> update(Authentication authentication,
                                               @PathVariable Long id,
                                               @Valid @RequestBody UpdateVehicleRequest request) {
        return ApiResponse.success("Vehicle updated successfully",
                vehicleService.update(authentication.getName(), id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<VehicleResponse> get(Authentication authentication, @PathVariable Long id) {
        return ApiResponse.success(vehicleService.get(authentication.getName(), id));
    }

    @GetMapping("/me")
    public ApiResponse<List<VehicleResponse>> getMyVehicles(Authentication authentication) {
        return ApiResponse.success(vehicleService.getMyVehicles(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    public ApiResponse<List<VehicleResponse>> getAll() {
        return ApiResponse.success(vehicleService.getAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
        vehicleService.delete(authentication.getName(), id);
        return ApiResponse.success("Vehicle deleted successfully", null);
    }
}
