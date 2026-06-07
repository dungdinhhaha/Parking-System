package com.parking.system.facade;

import com.parking.system.adapter.ai.PlateRecognitionImage;
import com.parking.system.adapter.ai.PlateRecognitionProvider;
import com.parking.system.adapter.ai.PlateRecognitionResult;
import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.response.ParkingSessionResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PlateRecognitionLog;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.RecognitionStatus;
import com.parking.system.enums.RecognitionType;
import com.parking.system.enums.SessionStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PlateRecognitionLogRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.strategy.allocation.AllocationRequest;
import com.parking.system.strategy.allocation.AllocationResult;
import com.parking.system.strategy.allocation.AllocationStrategyFactory;
import com.parking.system.storage.FileStorageService;
import com.parking.system.storage.StoredFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ParkingOperationFacade {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final PlateRecognitionLogRepository plateRecognitionLogRepository;
    private final FileStorageService fileStorageService;
    private final PlateRecognitionProvider plateRecognitionProvider;
    private final AllocationStrategyFactory allocationStrategyFactory;

    @Transactional
    public ParkingSessionResponse checkIn(String username, CheckInRequest request, MultipartFile plateImage) {
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        String resolvedPlateNumber = normalizePlateNumber(request.getPlateNumber());
        PlateRecognitionLog recognitionLog = null;
        if (plateImage != null && !plateImage.isEmpty()) {
            StoredFile storedFile = fileStorageService.storePlateImage(plateImage);
            PlateRecognitionResult recognitionResult = recognizePlate(plateImage, resolvedPlateNumber);
            recognitionLog = buildRecognitionLog(storedFile, recognitionResult, staff, resolvedPlateNumber);
            resolvedPlateNumber = recognitionLog.getConfirmedPlateNumber();
        }

        ensureNoActiveSession(resolvedPlateNumber);

        Vehicle vehicle = vehicleRepository.findByPlateNumberIgnoreCase(resolvedPlateNumber)
                .orElse(null);
        if (vehicle == null) {
            Vehicle created = new Vehicle();
            created.setPlateNumber(resolvedPlateNumber);
            created.setVehicleType(request.getVehicleType());
            vehicle = vehicleRepository.save(created);
        }
        vehicle.setVehicleType(request.getVehicleType());
        vehicle = vehicleRepository.save(vehicle);

        AllocationResult allocationResult = allocationStrategyFactory
                .getStrategy(request.getVehicleType())
                .allocate(AllocationRequest.builder()
                        .buildingCode(request.getBuildingCode())
                        .plateNumber(resolvedPlateNumber)
                        .vehicleType(request.getVehicleType())
                        .allocationTime(LocalDateTime.now())
                        .build());

        ParkingSession session = new ParkingSession();
        session.setTicketCode(generateTicketCode());
        session.setPlateNumber(resolvedPlateNumber);
        session.setVehicleType(request.getVehicleType());
        session.setCheckInTime(LocalDateTime.now());
        session.setEntryGate(request.getEntryGate());
        session.setStatus(SessionStatus.ACTIVE);
        session.setVehicle(vehicle);
        session.setAssignedZone(allocationResult.getAssignedZone());
        session.setAssignedSlot(allocationResult.getAssignedSlot());

        if (allocationResult.getMatchedReservation() != null) {
            session.setReservation(allocationResult.getMatchedReservation());
            allocationResult.getMatchedReservation().setParkingSession(session);
        }

        ParkingSession savedSession = parkingSessionRepository.save(session);

        if (recognitionLog != null) {
            recognitionLog.setParkingSession(savedSession);
            if (recognitionLog.getVehicle() == null) {
                recognitionLog.setVehicle(vehicle);
            }
            plateRecognitionLogRepository.save(recognitionLog);
        }

        return ParkingSessionResponse.builder()
                .id(savedSession.getId())
                .ticketCode(savedSession.getTicketCode())
                .plateNumber(savedSession.getPlateNumber())
                .vehicleType(savedSession.getVehicleType())
                .status(savedSession.getStatus())
                .entryGate(savedSession.getEntryGate())
                .checkInTime(savedSession.getCheckInTime())
                .zoneId(savedSession.getAssignedZone() != null ? savedSession.getAssignedZone().getId() : null)
                .zoneCode(savedSession.getAssignedZone() != null ? savedSession.getAssignedZone().getZoneCode() : null)
                .slotId(savedSession.getAssignedSlot() != null ? savedSession.getAssignedSlot().getId() : null)
                .slotCode(savedSession.getAssignedSlot() != null ? savedSession.getAssignedSlot().getSlotCode() : null)
                .reservationId(savedSession.getReservation() != null ? savedSession.getReservation().getId() : null)
                .reservationCode(savedSession.getReservation() != null ? savedSession.getReservation().getReservationCode() : null)
                .recognitionLogId(recognitionLog != null ? recognitionLog.getId() : null)
                .allocationSource(allocationResult.getSource())
                .createdAt(savedSession.getCreatedAt())
                .updatedAt(savedSession.getUpdatedAt())
                .build();
    }

    private void ensureNoActiveSession(String plateNumber) {
        if (parkingSessionRepository.existsByPlateNumberIgnoreCaseAndStatus(plateNumber, SessionStatus.ACTIVE)) {
            throw new BusinessException("Vehicle already has an active parking session");
        }
    }

    private PlateRecognitionResult recognizePlate(MultipartFile image, String fallbackPlateNumber) {
        try {
            return plateRecognitionProvider.recognize(PlateRecognitionImage.builder()
                    .data(image.getBytes())
                    .originalFileName(image.getOriginalFilename())
                    .contentType(image.getContentType())
                    .fallbackPlateNumber(fallbackPlateNumber)
                    .build());
        } catch (IOException ex) {
            throw new BusinessException("Failed to read plate image", ex);
        }
    }

    private PlateRecognitionLog buildRecognitionLog(StoredFile storedFile,
                                                    PlateRecognitionResult recognitionResult,
                                                    User staff,
                                                    String fallbackPlateNumber) {
        PlateRecognitionLog log = new PlateRecognitionLog();
        log.setImageUrl(storedFile.getPublicUrl());
        log.setOriginalFileName(storedFile.getOriginalFileName());
        log.setDetectedPlateNumber(recognitionResult.getPlateNumber());
        log.setConfidence(recognitionResult.getConfidence());
        log.setProvider(recognitionResult.getProvider());
        log.setRecognitionType(RecognitionType.CHECK_IN);
        log.setUploadedBy(staff);
        log.setStatus(recognitionResult.getConfidence() >= 0.5 ? RecognitionStatus.SUCCESS : RecognitionStatus.LOW_CONFIDENCE);
        if (recognitionResult.getConfidence() >= 0.5) {
            log.confirmPlate(recognitionResult.getPlateNumber());
        } else {
            log.setConfirmedPlateNumber(fallbackPlateNumber);
            log.setIsConfirmed(Boolean.FALSE);
        }
        return plateRecognitionLogRepository.save(log);
    }

    private String normalizePlateNumber(String plateNumber) {
        return plateNumber == null ? null : plateNumber.trim().toUpperCase(Locale.ROOT);
    }

    private String generateTicketCode() {
        return "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
