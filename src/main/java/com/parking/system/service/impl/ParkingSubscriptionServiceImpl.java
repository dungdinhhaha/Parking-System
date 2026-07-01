package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateParkingSubscriptionRequest;
import com.parking.system.dto.request.RenewParkingSubscriptionRequest;
import com.parking.system.dto.request.UpdateParkingSubscriptionRequest;
import com.parking.system.dto.response.ParkingSubscriptionHistoryResponse;
import com.parking.system.dto.response.ParkingSubscriptionResponse;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.BaseEntity;
import com.parking.system.entity.ParkingSubscription;
import com.parking.system.entity.ParkingSubscriptionHistory;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.SubscriptionStatus;
import com.parking.system.enums.UserRole;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.ParkingSubscriptionRepository;
import com.parking.system.repository.ParkingSubscriptionHistoryRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.service.ParkingSubscriptionService;
import com.parking.system.strategy.allocation.AllocationResult;
import com.parking.system.strategy.allocation.AllocationSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingSubscriptionServiceImpl implements ParkingSubscriptionService {

    private final ParkingSubscriptionRepository parkingSubscriptionRepository;
    private final ParkingSubscriptionHistoryRepository parkingSubscriptionHistoryRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParkingSubscriptionResponse create(String username, CreateParkingSubscriptionRequest request) {
        User currentUser = getUserByUsername(username);
        ensureAdmin(currentUser);

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new BusinessException("Vehicle not found"));

        if (parkingSubscriptionRepository.existsByVehicle_IdAndStatus(vehicle.getId(), SubscriptionStatus.ACTIVE)) {
            throw new BusinessException("Vehicle already has an active subscription");
        }
        String normalizedRfid = normalize(request.getRfidCardId());
        if (parkingSubscriptionRepository.existsByRfidCardIdIgnoreCaseAndStatus(normalizedRfid, SubscriptionStatus.ACTIVE)) {
            throw new BusinessException("RFID card already has an active subscription");
        }

        LocalDateTime startAt = request.getStartAt() != null ? request.getStartAt() : LocalDateTime.now();
        LocalDateTime endAt = startAt.plusDays(request.getSubscriptionType().getDays());

        ParkingSubscription subscription = new ParkingSubscription();
        subscription.setSubscriptionCode(generateSubscriptionCode());
        subscription.setRfidCardId(normalizedRfid);
        subscription.setSubscriberName(request.getSubscriberName().trim());
        subscription.setSubscriberPhone(request.getSubscriberPhone().trim());
        subscription.setSubscriptionType(request.getSubscriptionType());
        subscription.setVehicleType(vehicle.getVehicleType());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartAt(startAt);
        subscription.setEndAt(endAt);
        subscription.setNotes(request.getNotes());
        subscription.setVehicle(vehicle);
        subscription.setCreatedBy(currentUser);

        if (vehicle.getVehicleType() == VehicleType.CAR) {
            ParkingSlot slot = resolveCarSlot(request.getAssignedSlotId(), request.getAssignedZoneId());
            slot.lock();
            parkingSlotRepository.save(slot);
            subscription.setAssignedSlot(slot);
            subscription.setAssignedZone(slot.getZone());
        } else {
            ParkingZone zone = resolveMotorbikeZone(request.getAssignedZoneId(), request.getAssignedSlotId());
            zone.increaseReservedCount();
            parkingZoneRepository.save(zone);
            subscription.setAssignedZone(zone);
        }

        ParkingSubscription saved = parkingSubscriptionRepository.save(subscription);
        recordHistory(saved, "CREATE", null, null, null, null, currentUser, request.getNotes());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingSubscriptionResponse get(Long id) {
        return toResponse(parkingSubscriptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Subscription not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSubscriptionResponse> getAll() {
        return parkingSubscriptionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSubscriptionResponse> getMine(String username) {
        return parkingSubscriptionRepository
                .findAllByVehicle_Owner_UsernameIgnoreCaseOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingSubscriptionResponse findByPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }
        ParkingSubscription subscription = parkingSubscriptionRepository
                .findFirstByVehicle_PlateNumberIgnoreCaseOrderByCreatedAtDesc(normalize(plateNumber))
                .orElseThrow(() -> new BusinessException("Subscription not found for plate number"));
        return toResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSubscriptionResponse> findAllByPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }
        return parkingSubscriptionRepository
                .findAllByVehicle_PlateNumberIgnoreCaseOrderByCreatedAtDesc(normalize(plateNumber))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ParkingSubscriptionResponse cancel(String username, Long id) {
        User currentUser = getUserByUsername(username);
        ensureAdmin(currentUser);

        ParkingSubscription subscription = parkingSubscriptionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        if (hasActiveSession(subscription.getId())) {
            throw new BusinessException("Cannot cancel subscription while it is in use");
        }

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            return toResponse(subscription);
        }

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            releaseDedicatedResource(subscription);
        }
        subscription.cancel();
        ParkingSubscription saved = parkingSubscriptionRepository.save(subscription);
        recordHistory(saved, "CANCEL",
                saved.getRfidCardId(), saved.getVehicle(), saved.getAssignedZone(), saved.getAssignedSlot(),
                currentUser, "Subscription cancelled");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ParkingSubscriptionResponse renew(String username, Long id, RenewParkingSubscriptionRequest request) {
        User currentUser = getUserByUsername(username);
        ensureAdmin(currentUser);

        ParkingSubscription subscription = parkingSubscriptionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new BusinessException("Cannot renew a cancelled subscription");
        }
        if (parkingSubscriptionRepository.existsByRfidCardIdIgnoreCaseAndStatusAndIdNot(
                subscription.getRfidCardId(), SubscriptionStatus.ACTIVE, subscription.getId())) {
            throw new BusinessException("RFID card already has another active subscription");
        }
        if (parkingSubscriptionRepository.existsByVehicle_IdAndStatusAndIdNot(
                subscription.getVehicle().getId(), SubscriptionStatus.ACTIVE, subscription.getId())) {
            throw new BusinessException("Vehicle already has another active subscription");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean resourceWasReleased = subscription.getStatus() == SubscriptionStatus.EXPIRED;
        boolean wasExpired = resourceWasReleased
                || subscription.getEndAt() == null
                || subscription.getEndAt().isBefore(now);
        if (resourceWasReleased && !hasActiveSession(subscription.getId())) {
            reacquireDedicatedResource(subscription);
        }
        LocalDateTime baseEndAt = subscription.getEndAt();
        if (baseEndAt == null || baseEndAt.isBefore(now)) {
            baseEndAt = now;
        }

        subscription.setEndAt(baseEndAt.plusDays(request.getExtendDays()));
        subscription.activate();
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            subscription.setNotes(request.getNotes());
        }

        ParkingSubscription saved = parkingSubscriptionRepository.save(subscription);
        recordHistory(saved, "RENEW",
                saved.getRfidCardId(), saved.getVehicle(), saved.getAssignedZone(), saved.getAssignedSlot(),
                currentUser, request.getNotes());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ParkingSubscriptionResponse update(String username, Long id, UpdateParkingSubscriptionRequest request) {
        User currentUser = getUserByUsername(username);
        ensureAdmin(currentUser);

        ParkingSubscription subscription = parkingSubscriptionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Subscription not found"));
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new BusinessException("Cannot update a cancelled subscription");
        }
        if (hasActiveSession(subscription.getId())) {
            throw new BusinessException("Cannot update subscription while vehicle is in the parking lot");
        }

        String oldRfid = subscription.getRfidCardId();
        Vehicle oldVehicle = subscription.getVehicle();
        ParkingZone oldZone = subscription.getAssignedZone();
        ParkingSlot oldSlot = subscription.getAssignedSlot();

        Vehicle vehicle = request.getVehicleId() == null
                ? oldVehicle
                : vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new BusinessException("Vehicle not found"));
        if (!vehicle.getId().equals(oldVehicle.getId())
                && parkingSubscriptionRepository.existsByVehicle_IdAndStatusAndIdNot(
                        vehicle.getId(), SubscriptionStatus.ACTIVE, subscription.getId())) {
            throw new BusinessException("Vehicle already has another active subscription");
        }

        String rfid = request.getRfidCardId() == null ? oldRfid : normalize(request.getRfidCardId());
        if (rfid == null || rfid.isBlank()) {
            throw new BusinessException("RFID card id is required");
        }
        if (parkingSubscriptionRepository.existsByRfidCardIdIgnoreCaseAndStatusAndIdNot(
                rfid, SubscriptionStatus.ACTIVE, subscription.getId())) {
            throw new BusinessException("RFID card already has another active subscription");
        }

        boolean vehicleTypeChanged = vehicle.getVehicleType() != subscription.getVehicleType();
        boolean resourceRequested = request.getAssignedZoneId() != null || request.getAssignedSlotId() != null;
        if (vehicleTypeChanged || resourceRequested) {
            releaseDedicatedResource(subscription);
            subscription.setAssignedZone(null);
            subscription.setAssignedSlot(null);
            assignDedicatedResource(subscription, vehicle, request.getAssignedZoneId(), request.getAssignedSlotId());
        }

        subscription.setVehicle(vehicle);
        subscription.setVehicleType(vehicle.getVehicleType());
        subscription.setRfidCardId(rfid);
        if (request.getSubscriberName() != null) {
            subscription.setSubscriberName(request.getSubscriberName().trim());
        }
        if (request.getSubscriberPhone() != null) {
            subscription.setSubscriberPhone(request.getSubscriberPhone().trim());
        }
        if (request.getNotes() != null) {
            subscription.setNotes(request.getNotes());
        }

        ParkingSubscription saved = parkingSubscriptionRepository.save(subscription);
        recordHistory(saved, "UPDATE", oldRfid, oldVehicle, oldZone, oldSlot, currentUser, request.getNotes());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSubscriptionHistoryResponse> getHistory(Long id) {
        if (!parkingSubscriptionRepository.existsById(id)) {
            throw new BusinessException("Subscription not found");
        }
        return parkingSubscriptionHistoryRepository.findAllBySubscription_IdOrderByCreatedAtDesc(id).stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParkingSubscription> findActiveByRfidCardId(String rfidCardId, LocalDateTime at) {
        if (rfidCardId == null || rfidCardId.isBlank()) {
            return Optional.empty();
        }
        LocalDateTime resolvedAt = at != null ? at : LocalDateTime.now();
        return parkingSubscriptionRepository
                .findFirstByRfidCardIdIgnoreCaseAndStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqualOrderByCreatedAtDesc(
                        normalize(rfidCardId),
                        SubscriptionStatus.ACTIVE,
                        resolvedAt,
                        resolvedAt);
    }

    @Override
    @Transactional
    public Optional<AllocationResult> allocateForCheckIn(String rfidCardId, String plateNumber, LocalDateTime at) {
        if (rfidCardId == null || rfidCardId.isBlank()) {
            return Optional.empty();
        }
        LocalDateTime resolvedAt = at != null ? at : LocalDateTime.now();
        return parkingSubscriptionRepository
                .findActiveByRfidForUpdate(normalize(rfidCardId), SubscriptionStatus.ACTIVE, resolvedAt)
                .stream()
                .findFirst()
                .map(subscription -> allocate(subscription, plateNumber));
    }

    private AllocationResult allocate(ParkingSubscription subscription, String plateNumber) {
        Vehicle vehicle = subscription.getVehicle();
        if (vehicle == null) {
            throw new BusinessException("Subscription vehicle not found");
        }

        if (plateNumber != null && !normalize(plateNumber).equalsIgnoreCase(normalize(vehicle.getPlateNumber()))) {
            throw new BusinessException("Plate number does not match subscription vehicle");
        }

        if (vehicle.getVehicleType() == VehicleType.CAR) {
            ParkingSlot slot = subscription.getAssignedSlot() == null ? null
                    : parkingSlotRepository.findByIdForUpdate(subscription.getAssignedSlot().getId()).orElse(null);
            if (slot == null) {
                throw new BusinessException("Subscription slot not assigned");
            }
            if (slot.getStatus() == SlotStatus.OCCUPIED) {
                throw new BusinessException("Subscription slot is currently occupied");
            }
            slot.occupy();
            parkingSlotRepository.save(slot);
            return AllocationResult.builder()
                    .assignedZone(slot.getZone())
                    .assignedSlot(slot)
                    .source(AllocationSource.SUBSCRIPTION)
                    .build();
        }

        ParkingZone zone = subscription.getAssignedZone() == null ? null
                : parkingZoneRepository.findByIdForUpdate(subscription.getAssignedZone().getId()).orElse(null);
        if (zone == null) {
            throw new BusinessException("Subscription zone not assigned");
        }
        zone.increaseCurrentCount();
        parkingZoneRepository.save(zone);
        return AllocationResult.builder()
                .assignedZone(zone)
                .source(AllocationSource.SUBSCRIPTION)
                .build();
    }

    private void releaseDedicatedResource(ParkingSubscription subscription) {
        if (subscription.getVehicleType() == VehicleType.CAR) {
            ParkingSlot slot = subscription.getAssignedSlot() == null ? null
                    : parkingSlotRepository.findByIdForUpdate(subscription.getAssignedSlot().getId()).orElse(null);
            if (slot != null) {
                slot.release();
                parkingSlotRepository.save(slot);
            }
            return;
        }

        ParkingZone zone = subscription.getAssignedZone() == null ? null
                : parkingZoneRepository.findByIdForUpdate(subscription.getAssignedZone().getId()).orElse(null);
        if (zone != null) {
            zone.decreaseReservedCount();
            parkingZoneRepository.save(zone);
        }
    }

    private boolean hasActiveSession(Long subscriptionId) {
        return subscriptionId != null && parkingSessionRepositoryExistsBySubscription(subscriptionId);
    }

    private boolean parkingSessionRepositoryExistsBySubscription(Long subscriptionId) {
        return parkingSessionRepository.existsBySubscription_IdAndStatus(subscriptionId, com.parking.system.enums.SessionStatus.ACTIVE);
    }

    private ParkingSlot resolveCarSlot(Long assignedSlotId, Long assignedZoneId) {
        if (assignedSlotId == null) {
            throw new BusinessException("Assigned slot is required for car subscription");
        }
        ParkingSlot slot = parkingSlotRepository.findByIdForUpdate(assignedSlotId)
                .orElseThrow(() -> new BusinessException("Assigned slot not found"));
        if (slot.getVehicleType() != VehicleType.CAR) {
            throw new BusinessException("Assigned slot is not for CAR");
        }
        if (!slot.isAvailable()) {
            throw new BusinessException("Assigned slot is not available");
        }
        if (assignedZoneId != null && slot.getZone() != null && !slot.getZone().getId().equals(assignedZoneId)) {
            throw new BusinessException("Assigned zone does not match slot zone");
        }
        if (slot.getZone() != null && slot.getZone().getVehicleType() != VehicleType.CAR) {
            throw new BusinessException("Slot zone is not for CAR");
        }
        return slot;
    }

    private ParkingZone resolveMotorbikeZone(Long assignedZoneId, Long assignedSlotId) {
        if (assignedZoneId == null) {
            throw new BusinessException("Assigned zone is required for motorbike subscription");
        }
        if (assignedSlotId != null) {
            throw new BusinessException("Motorbike subscription cannot use assigned slot");
        }
        ParkingZone zone = parkingZoneRepository.findByIdForUpdate(assignedZoneId)
                .orElseThrow(() -> new BusinessException("Assigned zone not found"));
        if (zone.getVehicleType() != VehicleType.MOTORBIKE) {
            throw new BusinessException("Assigned zone is not for MOTORBIKE");
        }
        if (!zone.hasAvailableCapacity()) {
            throw new BusinessException("Assigned zone has no available capacity");
        }
        return zone;
    }

    private void assignDedicatedResource(ParkingSubscription subscription,
                                         Vehicle vehicle,
                                         Long assignedZoneId,
                                         Long assignedSlotId) {
        if (vehicle.getVehicleType() == VehicleType.CAR) {
            ParkingSlot slot = resolveCarSlot(assignedSlotId, assignedZoneId);
            slot.lock();
            parkingSlotRepository.save(slot);
            subscription.setAssignedSlot(slot);
            subscription.setAssignedZone(slot.getZone());
            return;
        }

        ParkingZone zone = resolveMotorbikeZone(assignedZoneId, assignedSlotId);
        zone.increaseReservedCount();
        parkingZoneRepository.save(zone);
        subscription.setAssignedZone(zone);
    }

    private void reacquireDedicatedResource(ParkingSubscription subscription) {
        if (subscription.getVehicleType() == VehicleType.CAR) {
            if (subscription.getAssignedSlot() == null) {
                throw new BusinessException("Subscription slot not assigned");
            }
            ParkingSlot slot = parkingSlotRepository.findByIdForUpdate(subscription.getAssignedSlot().getId())
                    .orElseThrow(() -> new BusinessException("Assigned slot not found"));
            if (!slot.isAvailable()) {
                throw new BusinessException("Previous subscription slot is no longer available");
            }
            slot.lock();
            parkingSlotRepository.save(slot);
            return;
        }

        if (subscription.getAssignedZone() == null) {
            throw new BusinessException("Subscription zone not assigned");
        }
        ParkingZone zone = parkingZoneRepository.findByIdForUpdate(subscription.getAssignedZone().getId())
                .orElseThrow(() -> new BusinessException("Assigned zone not found"));
        if (!zone.hasAvailableCapacity()) {
            throw new BusinessException("Previous subscription zone has no available capacity");
        }
        zone.increaseReservedCount();
        parkingZoneRepository.save(zone);
    }

    private void recordHistory(ParkingSubscription subscription,
                               String action,
                               String oldRfid,
                               Vehicle oldVehicle,
                               ParkingZone oldZone,
                               ParkingSlot oldSlot,
                               User changedBy,
                               String notes) {
        ParkingSubscriptionHistory history = new ParkingSubscriptionHistory();
        history.setSubscription(subscription);
        history.setAction(action);
        history.setOldRfidCardId(oldRfid);
        history.setNewRfidCardId(subscription.getRfidCardId());
        history.setOldVehicleId(idOf(oldVehicle));
        history.setNewVehicleId(idOf(subscription.getVehicle()));
        history.setOldZoneId(idOf(oldZone));
        history.setNewZoneId(idOf(subscription.getAssignedZone()));
        history.setOldSlotId(idOf(oldSlot));
        history.setNewSlotId(idOf(subscription.getAssignedSlot()));
        history.setChangedBy(changedBy);
        history.setNotes(notes);
        parkingSubscriptionHistoryRepository.save(history);
    }

    private ParkingSubscriptionHistoryResponse toHistoryResponse(ParkingSubscriptionHistory history) {
        return ParkingSubscriptionHistoryResponse.builder()
                .id(history.getId())
                .subscriptionId(history.getSubscription().getId())
                .action(history.getAction())
                .oldRfidCardId(history.getOldRfidCardId())
                .newRfidCardId(history.getNewRfidCardId())
                .oldVehicleId(history.getOldVehicleId())
                .newVehicleId(history.getNewVehicleId())
                .oldZoneId(history.getOldZoneId())
                .newZoneId(history.getNewZoneId())
                .oldSlotId(history.getOldSlotId())
                .newSlotId(history.getNewSlotId())
                .changedByUsername(history.getChangedBy() == null ? null : history.getChangedBy().getUsername())
                .notes(history.getNotes())
                .createdAt(history.getCreatedAt())
                .build();
    }

    private Long idOf(BaseEntity entity) {
        return entity == null ? null : entity.getId();
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private void ensureAdmin(User currentUser) {
        if (currentUser.getRole() != UserRole.MANAGER && currentUser.getRole() != UserRole.SYSTEM_ADMIN) {
            throw new BusinessException("Access denied");
        }
    }

    private ParkingSubscriptionResponse toResponse(ParkingSubscription subscription) {
        return ParkingSubscriptionResponse.builder()
                .id(subscription.getId())
                .subscriptionCode(subscription.getSubscriptionCode())
                .rfidCardId(subscription.getRfidCardId())
                .subscriberName(subscription.getSubscriberName())
                .subscriberPhone(subscription.getSubscriberPhone())
                .subscriptionType(subscription.getSubscriptionType())
                .status(subscription.getStatus())
                .vehicleType(subscription.getVehicleType())
                .vehicleId(subscription.getVehicle() != null ? subscription.getVehicle().getId() : null)
                .vehiclePlateNumber(subscription.getVehicle() != null ? subscription.getVehicle().getPlateNumber() : null)
                .assignedZoneId(subscription.getAssignedZone() != null ? subscription.getAssignedZone().getId() : null)
                .assignedZoneCode(subscription.getAssignedZone() != null ? subscription.getAssignedZone().getZoneCode() : null)
                .assignedSlotId(subscription.getAssignedSlot() != null ? subscription.getAssignedSlot().getId() : null)
                .assignedSlotCode(subscription.getAssignedSlot() != null ? subscription.getAssignedSlot().getSlotCode() : null)
                .startAt(subscription.getStartAt())
                .endAt(subscription.getEndAt())
                .createdById(subscription.getCreatedBy() != null ? subscription.getCreatedBy().getId() : null)
                .createdByUsername(subscription.getCreatedBy() != null ? subscription.getCreatedBy().getUsername() : null)
                .notes(subscription.getNotes())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private String generateSubscriptionCode() {
        return "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
