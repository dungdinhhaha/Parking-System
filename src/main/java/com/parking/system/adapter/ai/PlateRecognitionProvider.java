package com.parking.system.adapter.ai;

public interface PlateRecognitionProvider {
    PlateRecognitionResult recognize(PlateRecognitionImage image, String fallbackPlateNumber);
}
