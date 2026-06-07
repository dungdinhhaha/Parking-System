package com.parking.system.adapter.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlateRecognitionImage {
    private final byte[] data;
    private final String originalFileName;
    private final String contentType;
    private final String fallbackPlateNumber;
}
