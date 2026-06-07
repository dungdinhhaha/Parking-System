package com.parking.system.controller.pricing;

import com.parking.system.dto.request.CalculateFeeRequest;
import com.parking.system.dto.request.CreatePricingPolicyRequest;
import com.parking.system.dto.request.UpdatePricingPolicyRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.FeeCalculationResponse;
import com.parking.system.dto.response.PricingPolicyResponse;
import com.parking.system.enums.VehicleType;
import com.parking.system.service.FeeCalculationService;
import com.parking.system.service.PricingPolicyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingPolicyController {

    private final PricingPolicyService pricingPolicyService;
    private final FeeCalculationService feeCalculationService;

    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    @PostMapping("/policies")
    public ApiResponse<PricingPolicyResponse> create(@Valid @RequestBody CreatePricingPolicyRequest request) {
        return ApiResponse.success(201, "Pricing policy created successfully", pricingPolicyService.create(request));
    }

    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    @PutMapping("/policies/{id}")
    public ApiResponse<PricingPolicyResponse> update(@PathVariable Long id, @Valid @RequestBody UpdatePricingPolicyRequest request) {
        return ApiResponse.success("Pricing policy updated successfully", pricingPolicyService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    @GetMapping("/policies/{id}")
    public ApiResponse<PricingPolicyResponse> get(@PathVariable Long id) {
        return ApiResponse.success(pricingPolicyService.get(id));
    }

    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    @GetMapping("/policies")
    public ApiResponse<List<PricingPolicyResponse>> getAll() {
        return ApiResponse.success(pricingPolicyService.getAll());
    }

    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
    @GetMapping("/policies/active/{vehicleType}")
    public ApiResponse<PricingPolicyResponse> getActive(@PathVariable VehicleType vehicleType) {
        return ApiResponse.success(pricingPolicyService.getActivePolicy(vehicleType));
    }

    @PreAuthorize("hasAnyRole('STAFF','MANAGER','SYSTEM_ADMIN')")
    @PostMapping("/fees/calculate")
    public ApiResponse<FeeCalculationResponse> calculate(@Valid @RequestBody CalculateFeeRequest request) {
        return ApiResponse.success("Fee calculated successfully", feeCalculationService.calculate(request));
    }
}
