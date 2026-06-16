package com.parking.system.strategy.allocation;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ReservationRepository;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CarGreedySlotAllocationStrategy implements ParkingAllocationStrategy {

    private enum AllocationMode {
        MORNING_PEAK,
        NORMAL_BALANCED,
        EVENING_PEAK
    }

    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public CarGreedySlotAllocationStrategy(ReservationRepository reservationRepository,
                                           ParkingSlotRepository parkingSlotRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    @Override
    public VehicleType supports() {
        return VehicleType.CAR;
    }

    @Override
    @Transactional
    public AllocationResult allocate(AllocationRequest request) {
        Reservation reservation = findReservation(request);
        if (reservation != null) {
            return allocateFromReservation(request, reservation);
        }

        AllocationMode mode = resolveMode(request.getAllocationTime());

        List<ParkingSlot> candidates = parkingSlotRepository
                .findAllAvailableForUpdate(request.getBuildingCode(), SlotStatus.AVAILABLE)
                .stream()
                .filter(candidate -> candidate.getVehicleType() == VehicleType.CAR)
                .toList();

        ParkingSlot slot = candidates.stream()
                .sorted(Comparator
                        .comparingDouble((ParkingSlot candidate) -> scoreSlot(candidate, mode))
                        .reversed()
                        .thenComparing(ParkingSlot::getDistanceFromGate, Comparator.nullsLast(Double::compareTo))
                        .thenComparing(ParkingSlot::getSlotCode, Comparator.nullsLast(String::compareToIgnoreCase)))
                .findFirst()
                .orElseThrow(() -> new BusinessException("No available car slot"));

        slot.occupy();
        ParkingSlot savedSlot = parkingSlotRepository.save(slot);
        return AllocationResult.builder()
                .assignedZone(savedSlot.getZone())
                .assignedSlot(savedSlot)
                .source(sourceOf(mode))
                .build();
    }

    private Reservation findReservation(AllocationRequest request) {
        return reservationRepository
                .findMatchingForUpdate(
                        request.getPlateNumber(),
                        request.getVehicleType(),
                        ReservationStatus.CONFIRMED,
                        request.getAllocationTime())
                .stream()
                .filter(reservation -> reservation.getAssignedSlot() != null)
                .filter(reservation -> reservation.getAssignedSlot().getZone().getFloor().getBuilding().getBuildingCode()
                        .equalsIgnoreCase(request.getBuildingCode()))
                .findFirst()
                .orElse(null);
    }

    private AllocationResult allocateFromReservation(AllocationRequest request, Reservation reservation) {
        ParkingSlot slot = parkingSlotRepository.findByIdForUpdate(reservation.getAssignedSlot().getId())
                .orElseThrow(() -> new BusinessException("Reserved slot not found"));
        if (!slot.isReserved()) {
            throw new BusinessException("Reserved slot is not available");
        }

        slot.occupyReserved();
        ParkingSlot savedSlot = parkingSlotRepository.save(slot);
        reservation.markUsed();
        reservationRepository.save(reservation);

        return AllocationResult.builder()
                .assignedZone(savedSlot.getZone())
                .assignedSlot(savedSlot)
                .matchedReservation(reservation)
                .source(AllocationSource.RESERVATION)
                .build();
    }

    private AllocationSource sourceOf(AllocationMode mode) {
        return switch (mode) {
            default -> AllocationSource.GREEDY;
        };
    }

    private AllocationMode resolveMode(LocalDateTime allocationTime) {
        LocalTime time = allocationTime == null ? LocalTime.now() : allocationTime.toLocalTime();
        if (!time.isBefore(LocalTime.of(6, 30)) && !time.isAfter(LocalTime.of(9, 0))) {
            return AllocationMode.MORNING_PEAK;
        }
        if (!time.isBefore(LocalTime.of(16, 30)) && !time.isAfter(LocalTime.of(19, 30))) {
            return AllocationMode.EVENING_PEAK;
        }
        return AllocationMode.NORMAL_BALANCED;
    }

    private double scoreSlot(ParkingSlot slot, AllocationMode mode) {
        double distanceScore = inverseDistance(slot.getDistanceFromGate());
        int capacity = slot.getZone().getCapacity() == null ? 0 : slot.getZone().getCapacity();
        double fillRatio = capacity <= 0 ? 0.0 : clamp(slot.getZone().getCurrentCount() / (double) capacity);
        double freeRatio = 1.0 - fillRatio;

        return switch (mode) {
            case MORNING_PEAK -> (0.75 * distanceScore) + (0.25 * freeRatio);
            case NORMAL_BALANCED -> (0.70 * proximityToTarget(fillRatio, 0.85)) + (0.30 * freeRatio);
            case EVENING_PEAK -> (0.60 * distanceScore) + (0.40 * freeRatio);
            default -> 1.0;
        };
    }

    private double inverseDistance(Double distance) {
        if (distance == null || distance.isNaN() || distance.isInfinite()) {
            return 0.0;
        }
        return 1.0 / (1.0 + Math.max(0.0, distance));
    }

    private double proximityToTarget(double value, double target) {
        return 1.0 - Math.min(1.0, Math.abs(clamp(value) - clamp(target)));
    }

    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }
}
