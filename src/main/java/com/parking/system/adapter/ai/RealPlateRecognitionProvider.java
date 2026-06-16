package com.parking.system.adapter.ai;

import org.springframework.stereotype.Component;

@Component
public class RealPlateRecognitionProvider implements PlateRecognitionProvider {

    @Override
    public PlateRecognitionResult recognize(PlateRecognitionImage image, String fallbackPlateNumber) {
        return PlateRecognitionResult.builder()
                .plateNumber(fallbackPlateNumber)
                .confidence(0.0)
                .provider("REAL-STUB")
                .build();
    }
}
