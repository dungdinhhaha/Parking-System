package com.parking.system.controller.ai;

import com.parking.system.adapter.ai.PlateRecognitionImage;
import com.parking.system.adapter.ai.PlateRecognitionProvider;
import com.parking.system.adapter.ai.PlateRecognitionResult;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.PlateRecognitionResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai/plate-recognition")
@PreAuthorize("hasAnyRole('STAFF','MANAGER','SYSTEM_ADMIN')")
@RequiredArgsConstructor
public class PlateRecognitionController {

    private final PlateRecognitionProvider plateRecognitionProvider;

    @PostMapping(value = "/mock", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PlateRecognitionResponse> recognizeMock(
            @RequestParam(value = "fallbackPlateNumber", required = false) String fallbackPlateNumber,
            @RequestParam(value = "plateImage", required = false) MultipartFile plateImage) {
        PlateRecognitionResult result = recognize(plateImage, fallbackPlateNumber);
        PlateRecognitionResponse response = PlateRecognitionResponse.builder()
                .plateNumber(result.getPlateNumber())
                .confidence(result.getConfidence())
                .provider(result.getProvider())
                .fromImage(plateImage != null && !plateImage.isEmpty())
                .build();
        return ApiResponse.success("Plate recognition completed successfully", response);
    }

    private PlateRecognitionResult recognize(MultipartFile plateImage, String fallbackPlateNumber) {
        try {
            byte[] bytes = plateImage == null ? new byte[0] : plateImage.getBytes();
            return plateRecognitionProvider.recognize(PlateRecognitionImage.builder()
                    .data(bytes)
                    .originalFileName(plateImage != null ? plateImage.getOriginalFilename() : null)
                    .contentType(plateImage != null ? plateImage.getContentType() : null)
                    .fallbackPlateNumber(fallbackPlateNumber)
                    .build());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read plate image", ex);
        }
    }
}
