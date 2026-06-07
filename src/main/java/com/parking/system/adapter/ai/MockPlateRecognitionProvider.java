package com.parking.system.adapter.ai;

import org.springframework.stereotype.Component;

@Component
public class MockPlateRecognitionProvider implements PlateRecognitionProvider {

    @Override
    public PlateRecognitionResult recognize(PlateRecognitionImage image) {
        String plateNumber = image.getFallbackPlateNumber() == null || image.getFallbackPlateNumber().isBlank()
                ? "UNKNOWN"
                : image.getFallbackPlateNumber().trim().toUpperCase();
        double confidence = image.getData() == null || image.getData().length == 0 ? 0.0 : 0.99;

        return PlateRecognitionResult.builder()
                .plateNumber(plateNumber)
                .confidence(confidence)
                .provider("MOCK_AI")
                .build();
    }
}
