package com.parking.system.service.checkin;

import com.parking.system.adapter.ai.PlateRecognitionResult;
import com.parking.system.strategy.allocation.AllocationResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckInCombinedResult {
    private final String resolvedPlateNumber;
    private final PlateRecognitionResult recognitionResult;
    private final AllocationResult allocationResult;
}
