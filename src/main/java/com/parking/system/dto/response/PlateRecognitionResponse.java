package com.parking.system.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlateRecognitionResponse {
    private final String plateNumber;
    private final double confidence;
    private final String provider;
    private final boolean fromImage;
}
