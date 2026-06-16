package com.parking.system.service.checkin;

import com.parking.system.adapter.ai.PlateRecognitionImage;
import com.parking.system.adapter.ai.PlateRecognitionProvider;
import com.parking.system.adapter.ai.PlateRecognitionResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CheckInRecognitionTask {

    private final PlateRecognitionProvider provider;

    public CheckInRecognitionTask(PlateRecognitionProvider provider) {
        this.provider = provider;
    }

    public PlateRecognitionResult recognize(PlateRecognitionImage image, String fallbackPlateNumber) {
        return provider.recognize(image, fallbackPlateNumber);
    }

    public CompletableFuture<PlateRecognitionResult> recognizeAsync(MultipartFile image, String fallbackPlateNumber, Executor executor) {
        PlateRecognitionImage recognitionImage = image == null ? null : new PlateRecognitionImage(image);
        return CompletableFuture.supplyAsync(() -> provider.recognize(recognitionImage, fallbackPlateNumber), executor);
    }
}
