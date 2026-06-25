package com.parking.system.service.checkin;

import com.parking.system.adapter.ai.PlateRecognitionImage;
import com.parking.system.adapter.ai.PlateRecognitionProvider;
import com.parking.system.adapter.ai.PlateRecognitionResult;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CheckInRecognitionTask {

    private final PlateRecognitionProvider plateRecognitionProvider;

    public CheckInRecognitionTask(PlateRecognitionProvider plateRecognitionProvider) {
        this.plateRecognitionProvider = plateRecognitionProvider;
    }

    public CompletableFuture<PlateRecognitionResult> recognizeAsync(MultipartFile plateImage, String fallbackPlateNumber,
                                                                     java.util.concurrent.Executor executor) {
        return CompletableFuture.supplyAsync(() -> recognize(plateImage, fallbackPlateNumber), executor);
    }

    public PlateRecognitionResult recognize(MultipartFile plateImage, String fallbackPlateNumber) {
        try {
            byte[] bytes = plateImage == null ? new byte[0] : plateImage.getBytes();
            String normalizedFallback = normalize(fallbackPlateNumber);
            return plateRecognitionProvider.recognize(PlateRecognitionImage.builder()
                    .data(bytes)
                    .originalFileName(plateImage != null ? plateImage.getOriginalFilename() : null)
                    .contentType(plateImage != null ? plateImage.getContentType() : null)
                    .fallbackPlateNumber(normalizedFallback)
                    .build());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read plate image", ex);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
