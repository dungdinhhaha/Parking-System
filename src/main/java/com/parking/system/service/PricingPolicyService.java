package com.parking.system.service;

import com.parking.system.dto.request.CreatePricingPolicyRequest;
import com.parking.system.dto.request.UpdatePricingPolicyRequest;
import com.parking.system.dto.response.PricingPolicyResponse;
import com.parking.system.enums.VehicleType;
import java.util.List;

public interface PricingPolicyService {
    PricingPolicyResponse create(CreatePricingPolicyRequest request);

    PricingPolicyResponse update(Long id, UpdatePricingPolicyRequest request);

    PricingPolicyResponse get(Long id);

    List<PricingPolicyResponse> getAll();

    PricingPolicyResponse getActivePolicy(VehicleType vehicleType);
}
