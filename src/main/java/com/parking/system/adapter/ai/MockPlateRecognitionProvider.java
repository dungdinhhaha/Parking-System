package com.parking.system.adapter.ai;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class MockPlateRecognitionProvider implements PlateRecognitionProvider {

    @Override
    public PlateRecognitionResult recognize(PlateRecognitionImage image, String fallbackPlateNumber) {
        return PlateRecognitionResult.builder()
                .plateNumber(fallbackPlateNumber)
                .confidence(1.0)
                .provider("MOCK")
                .build();
    }
}
