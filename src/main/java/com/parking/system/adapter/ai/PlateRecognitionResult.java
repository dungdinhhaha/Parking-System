package com.parking.system.adapter.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlateRecognitionResult {
    private final String plateNumber;
    private final double confidence;
    private final String provider;
}
