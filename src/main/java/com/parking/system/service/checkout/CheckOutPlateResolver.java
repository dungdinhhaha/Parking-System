package com.parking.system.service.checkout;

import com.parking.system.adapter.ai.PlateRecognitionImage;
import com.parking.system.adapter.ai.PlateRecognitionProvider;
import com.parking.system.adapter.ai.PlateRecognitionResult;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PlateRecognitionLog;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.RecognitionStatus;
import com.parking.system.enums.RecognitionType;
import com.parking.system.repository.PlateRecognitionLogRepository;
import com.parking.system.storage.FileStorageService;
import com.parking.system.storage.StoredFile;
import java.io.IOException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class CheckOutPlateResolver {

    private final FileStorageService fileStorageService;
    private final PlateRecognitionProvider plateRecognitionProvider;
    private final PlateRecognitionLogRepository plateRecognitionLogRepository;

    public String resolve(ParkingSession session, User staff, MultipartFile plateImage, String fallbackPlateNumber) {
        if (plateImage == null || plateImage.isEmpty()) {
            return normalize(fallbackPlateNumber != null ? fallbackPlateNumber : session.getPlateNumber());
        }

        StoredFile storedFile = fileStorageService.storePlateImage(plateImage);
        PlateRecognitionResult result = recognize(plateImage, fallbackPlateNumber != null ? fallbackPlateNumber : session.getPlateNumber());

        PlateRecognitionLog log = new PlateRecognitionLog();
        log.setImageUrl(storedFile.getPublicUrl());
        log.setOriginalFileName(storedFile.getOriginalFileName());
        log.setDetectedPlateNumber(result.getPlateNumber());
        log.setConfidence(result.getConfidence());
        log.setProvider(result.getProvider());
        log.setRecognitionType(RecognitionType.CHECK_OUT);
        log.setUploadedBy(staff);
        log.setVehicle(session.getVehicle());
        log.setStatus(result.getConfidence() >= 0.5 ? RecognitionStatus.SUCCESS : RecognitionStatus.LOW_CONFIDENCE);
        if (result.getConfidence() >= 0.5) {
            log.confirmPlate(result.getPlateNumber());
        } else {
            log.setConfirmedPlateNumber(normalize(fallbackPlateNumber != null ? fallbackPlateNumber : session.getPlateNumber()));
            log.setIsConfirmed(Boolean.FALSE);
        }
        plateRecognitionLogRepository.save(log);
        return log.getConfirmedPlateNumber();
    }

    private PlateRecognitionResult recognize(MultipartFile image, String fallbackPlateNumber) {
        try {
            return plateRecognitionProvider.recognize(PlateRecognitionImage.builder()
                    .data(image.getBytes())
                    .originalFileName(image.getOriginalFilename())
                    .contentType(image.getContentType())
                    .fallbackPlateNumber(normalize(fallbackPlateNumber))
                    .build());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read plate image", ex);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
