package com.parking.system.adapter.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlateRecognitionResult {
    private final String plateNumber;
    private final Double confidence;
    private final String provider;
}
