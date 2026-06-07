package com.parking.system.service;

import com.parking.system.dto.request.CalculateFeeRequest;
import com.parking.system.dto.response.FeeCalculationResponse;

public interface FeeCalculationService {
    FeeCalculationResponse calculate(CalculateFeeRequest request);
}
