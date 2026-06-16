package com.parking.system.service.checkin;

import com.parking.system.adapter.ai.PlateRecognitionResult;
import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.response.ParkingSessionResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PlateRecognitionLog;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.GateActionType;
import com.parking.system.enums.RecognitionStatus;
import com.parking.system.enums.RecognitionType;
import com.parking.system.enums.SessionStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PlateRecognitionLogRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.storage.FileStorageService;
import com.parking.system.storage.StoredFile;
import com.parking.system.service.ParkingSubscriptionService;
import com.parking.system.service.operations.OperationsRealtimeService;
import com.parking.system.strategy.allocation.AllocationRequest;
import com.parking.system.strategy.allocation.AllocationResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CheckInOrchestrationService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final PlateRecognitionLogRepository plateRecognitionLogRepository;
    private final FileStorageService fileStorageService;
    private final CheckInRecognitionTask recognitionTask;
    private final CheckInAllocationTask allocationTask;
    private final ParkingSubscriptionService parkingSubscriptionService;
    private final OperationsRealtimeService operationsRealtimeService;
    private final Executor executor;
    private final long recognitionTimeoutMs;

    public CheckInOrchestrationService(UserRepository userRepository,
                                       VehicleRepository vehicleRepository,
                                       ParkingSessionRepository parkingSessionRepository,
                                       PlateRecognitionLogRepository plateRecognitionLogRepository,
                                       FileStorageService fileStorageService,
                                       CheckInRecognitionTask recognitionTask,
                                       CheckInAllocationTask allocationTask,
                                       ParkingSubscriptionService parkingSubscriptionService,
                                       OperationsRealtimeService operationsRealtimeService,
                                       @Qualifier("checkInTaskExecutor") Executor executor,
                                       @Value("${parking.check-in.recognition-timeout-ms:5000}") long recognitionTimeoutMs) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.plateRecognitionLogRepository = plateRecognitionLogRepository;
        this.fileStorageService = fileStorageService;
        this.recognitionTask = recognitionTask;
        this.allocationTask = allocationTask;
        this.parkingSubscriptionService = parkingSubscriptionService;
        this.operationsRealtimeService = operationsRealtimeService;
        this.executor = executor;
        this.recognitionTimeoutMs = recognitionTimeoutMs;
    }

    @Transactional
    public ParkingSessionResponse checkIn(String username, CheckInRequest request, MultipartFile plateImage) {
        validateAction(request);
        String requestId = normalizeRequestId(request.getRequestId());
        if (requestId != null) {
            ParkingSession existing = parkingSessionRepository.findByCheckInRequestId(requestId).orElse(null);
            if (existing != null) {
                return toResponse(existing, null, null);
            }
        }

        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime allocationTime = request.getAllocationTime() != null ? request.getAllocationTime() : LocalDateTime.now();
        String fallbackPlateNumber = normalize(request.getPlateNumber());

        try {
            ensureNoActiveSession(fallbackPlateNumber, request.getRfidCardId());

            StoredFile storedFile = null;
            if (plateImage != null && !plateImage.isEmpty()) {
                storedFile = storePlateImage(plateImage);
            }

            CompletableFuture<PlateRecognitionResult> recognitionFuture = plateImage == null || plateImage.isEmpty()
                    ? CompletableFuture.completedFuture(recognitionTask.recognize(null, fallbackPlateNumber))
                    : recognitionTask.recognizeAsync(plateImage, fallbackPlateNumber, executor)
                            .orTimeout(recognitionTimeoutMs, TimeUnit.MILLISECONDS);

            String normalizedRfid = normalize(request.getRfidCardId());
            var activeSubscription = parkingSubscriptionService
                    .findActiveByRfidCardId(normalizedRfid, allocationTime)
                    .orElse(null);
            if (activeSubscription != null && activeSubscription.getVehicleType() != request.getVehicleType()) {
                throw new BusinessException("Subscription vehicle type does not match check-in request");
            }
            AllocationResult regularAllocation = null;
            if (activeSubscription == null) {
                regularAllocation = allocationTask.allocate(AllocationRequest.builder()
                        .buildingCode(request.getBuildingCode())
                        .plateNumber(fallbackPlateNumber)
                        .vehicleType(request.getVehicleType())
                        .allocationTime(allocationTime)
                        .build());
            }

            PlateRecognitionResult recognitionResult;
            try {
                recognitionResult = recognitionFuture.join();
            } catch (CompletionException ex) {
                throw new BusinessException("Plate recognition failed or timed out", ex.getCause());
            }
            String resolvedPlateNumber = resolvePlateNumber(recognitionResult, fallbackPlateNumber);

            AllocationResult resolvedAllocation;
            Vehicle vehicle;
            if (activeSubscription != null) {
                resolvedAllocation = parkingSubscriptionService
                        .allocateForCheckIn(normalizedRfid, resolvedPlateNumber, allocationTime)
                        .orElseThrow(() -> new BusinessException("Failed to allocate subscription resource"));
                vehicle = activeSubscription.getVehicle();
            } else {
                resolvedAllocation = regularAllocation;
                vehicle = findOrCreateVehicle(resolvedPlateNumber, request);
            }

            ParkingSession session = new ParkingSession();
            session.setTicketCode(generateTicketCode());
            session.setCheckInRequestId(requestId);
            session.setPlateNumber(resolvedPlateNumber);
            session.setRfidCardId(normalizedRfid);
            session.setVehicleType(request.getVehicleType());
            session.setCheckInTime(allocationTime);
            session.setEntryGate(request.getEntryGate());
            session.setStatus(SessionStatus.ACTIVE);
            session.setVehicle(vehicle);
            session.setAssignedZone(resolvedAllocation.getAssignedZone());
            session.setAssignedSlot(resolvedAllocation.getAssignedSlot());

            if (resolvedAllocation.getMatchedReservation() != null) {
                session.setReservation(resolvedAllocation.getMatchedReservation());
                resolvedAllocation.getMatchedReservation().setParkingSession(session);
            }

            if (activeSubscription != null) {
                session.setSubscription(activeSubscription);
            }

            ParkingSession savedSession = parkingSessionRepository.save(session);

            Long recognitionLogId = null;
            if (storedFile != null) {
                recognitionLogId = saveRecognitionLog(storedFile, recognitionResult, staff, resolvedPlateNumber, savedSession, vehicle)
                        .getId();
            }

            operationsRealtimeService.publishCheckInSuccess(
                    request.getBuildingCode(),
                    savedSession,
                    String.valueOf(resolvedAllocation.getSource()),
                    Duration.between(startedAt, LocalDateTime.now()).toMillis());

            return toResponse(savedSession, recognitionLogId, resolvedAllocation.getSource());
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    private void validateAction(CheckInRequest request) {
        if (request.getAction() != null && request.getAction() != GateActionType.CHECK_IN) {
            throw new BusinessException("Invalid gate action for check-in");
        }
    }

    private void ensureNoActiveSession(String plateNumber, String rfidCardId) {
        if (plateNumber != null && parkingSessionRepository.existsByPlateNumberIgnoreCaseAndStatus(plateNumber, SessionStatus.ACTIVE)) {
            throw new BusinessException("Vehicle already has an active parking session");
        }
        if (rfidCardId != null && parkingSessionRepository.existsByRfidCardIdIgnoreCaseAndStatus(rfidCardId, SessionStatus.ACTIVE)) {
            throw new BusinessException("RFID card already has an active parking session");
        }
    }

    private Vehicle findOrCreateVehicle(String plateNumber, CheckInRequest request) {
        Vehicle vehicle = vehicleRepository.findByPlateNumberIgnoreCase(plateNumber)
                .orElse(null);
        if (vehicle == null) {
            Vehicle created = new Vehicle();
            created.setPlateNumber(plateNumber);
            created.setVehicleType(request.getVehicleType());
            vehicle = vehicleRepository.save(created);
        }
        vehicle.setVehicleType(request.getVehicleType());
        return vehicleRepository.save(vehicle);
    }

    private StoredFile storePlateImage(MultipartFile plateImage) {
        try {
            return fileStorageService.storePlateImage(plateImage);
        } catch (Exception ex) {
            throw new BusinessException("Failed to store plate image", ex);
        }
    }

    private PlateRecognitionLog saveRecognitionLog(StoredFile storedFile,
                                                   PlateRecognitionResult recognitionResult,
                                                   User staff,
                                                   String resolvedPlateNumber,
                                                   ParkingSession session,
                                                   Vehicle vehicle) {
        PlateRecognitionLog log = new PlateRecognitionLog();
        log.setImageUrl(storedFile.getPublicUrl());
        log.setOriginalFileName(storedFile.getOriginalFileName());
        log.setDetectedPlateNumber(recognitionResult.getPlateNumber());
        log.setConfidence(recognitionResult.getConfidence());
        log.setProvider(recognitionResult.getProvider());
        log.setRecognitionType(RecognitionType.CHECK_IN);
        log.setUploadedBy(staff);
        log.setParkingSession(session);
        log.setVehicle(vehicle);
        log.setStatus(recognitionResult.getConfidence() >= 0.5 ? RecognitionStatus.SUCCESS : RecognitionStatus.LOW_CONFIDENCE);
        if (recognitionResult.getConfidence() >= 0.5) {
            log.confirmPlate(recognitionResult.getPlateNumber());
        } else {
            log.setConfirmedPlateNumber(resolvedPlateNumber);
            log.setIsConfirmed(Boolean.FALSE);
        }
        return plateRecognitionLogRepository.save(log);
    }

    private String resolvePlateNumber(PlateRecognitionResult recognitionResult, String fallbackPlateNumber) {
        if (recognitionResult != null && recognitionResult.getConfidence() >= 0.5
                && recognitionResult.getPlateNumber() != null && !recognitionResult.getPlateNumber().isBlank()) {
            return normalize(recognitionResult.getPlateNumber());
        }
        return normalize(fallbackPlateNumber);
    }

    private String normalize(String plateNumber) {
        return plateNumber == null ? null : plateNumber.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeRequestId(String requestId) {
        return requestId == null || requestId.isBlank() ? null : requestId.trim();
    }

    private ParkingSessionResponse toResponse(ParkingSession session,
                                              Long recognitionLogId,
                                              com.parking.system.strategy.allocation.AllocationSource allocationSource) {
        return ParkingSessionResponse.builder()
                .id(session.getId())
                .ticketCode(session.getTicketCode())
                .checkInRequestId(session.getCheckInRequestId())
                .plateNumber(session.getPlateNumber())
                .rfidCardId(session.getRfidCardId())
                .vehicleType(session.getVehicleType())
                .status(session.getStatus())
                .entryGate(session.getEntryGate())
                .checkInTime(session.getCheckInTime())
                .zoneId(session.getAssignedZone() != null ? session.getAssignedZone().getId() : null)
                .zoneCode(session.getAssignedZone() != null ? session.getAssignedZone().getZoneCode() : null)
                .slotId(session.getAssignedSlot() != null ? session.getAssignedSlot().getId() : null)
                .slotCode(session.getAssignedSlot() != null ? session.getAssignedSlot().getSlotCode() : null)
                .reservationId(session.getReservation() != null ? session.getReservation().getId() : null)
                .reservationCode(session.getReservation() != null ? session.getReservation().getReservationCode() : null)
                .subscriptionId(session.getSubscription() != null ? session.getSubscription().getId() : null)
                .subscriptionCode(session.getSubscription() != null ? session.getSubscription().getSubscriptionCode() : null)
                .subscriptionType(session.getSubscription() != null ? session.getSubscription().getSubscriptionType() : null)
                .recognitionLogId(recognitionLogId)
                .allocationSource(allocationSource)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    private String generateTicketCode() {
        return "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
