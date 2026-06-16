package com.parking.system.strategy.allocation;

import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ReservationRepository;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MotorbikeGreedyZoneAllocationStrategy implements ParkingAllocationStrategy {

    private enum AllocationMode {
        MORNING_PEAK,
        NORMAL_BALANCED,
        EVENING_PEAK
    }

    private final ReservationRepository reservationRepository;
    private final ParkingZoneRepository parkingZoneRepository;

    public MotorbikeGreedyZoneAllocationStrategy(ReservationRepository reservationRepository,
                                                 ParkingZoneRepository parkingZoneRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingZoneRepository = parkingZoneRepository;
    }

    @Override
    public VehicleType supports() {
        return VehicleType.MOTORBIKE;
    }

    @Override
    @Transactional
    public AllocationResult allocate(AllocationRequest request) {
        Reservation reservation = findReservation(request);
        if (reservation != null) {
            return allocateFromReservation(request, reservation);
        }

        AllocationMode mode = resolveMode(request.getAllocationTime());

        List<ParkingZone> candidates = parkingZoneRepository
                .findAllAvailableForUpdate(
                        request.getBuildingCode(),
                        VehicleType.MOTORBIKE,
                        ZoneStatus.AVAILABLE)
                .stream()
                .filter(ParkingZone::hasAvailableCapacity)
                .toList();

        ParkingZone zone = candidates.stream()
                .sorted(Comparator
                        .comparingDouble((ParkingZone candidate) -> scoreZone(candidate, mode))
                        .reversed()
                        .thenComparing((ParkingZone candidate) -> candidate.getAvailableCapacity(), Comparator.reverseOrder())
                        .thenComparing(candidate -> candidate.getZoneCode(), Comparator.nullsLast(String::compareToIgnoreCase)))
                .findFirst()
                .orElseThrow(() -> new BusinessException("No available motorbike zone"));

        zone.increaseCurrentCount();
        ParkingZone savedZone = parkingZoneRepository.save(zone);
        return AllocationResult.builder()
                .assignedZone(savedZone)
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
                .filter(reservation -> reservation.getAssignedZone() != null)
                .filter(reservation -> reservation.getAssignedZone().getFloor().getBuilding().getBuildingCode()
                        .equalsIgnoreCase(request.getBuildingCode()))
                .findFirst()
                .orElse(null);
    }

    private AllocationResult allocateFromReservation(AllocationRequest request, Reservation reservation) {
        ParkingZone zone = parkingZoneRepository.findByIdForUpdate(reservation.getAssignedZone().getId())
                .orElseThrow(() -> new BusinessException("Reserved zone not found"));
        if (zone.getReservedCount() <= 0) {
            throw new BusinessException("Reserved zone is not available");
        }

        zone.decreaseReservedCount();
        zone.increaseCurrentCount();
        ParkingZone savedZone = parkingZoneRepository.save(zone);
        reservation.markUsed();
        reservationRepository.save(reservation);

        return AllocationResult.builder()
                .assignedZone(savedZone)
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

    private double scoreZone(ParkingZone zone, AllocationMode mode) {
        double distanceScore = inverseDistance(zoneDistance(zone));
        int capacity = zone.getCapacity() == null ? 0 : zone.getCapacity();
        double fillRatio = capacity <= 0 ? 0.0 : clamp(zone.getCurrentCount() / (double) capacity);
        double freeRatio = capacity <= 0 ? 0.0 : clamp(zone.getAvailableCapacity() / (double) capacity);

        return switch (mode) {
            case MORNING_PEAK -> (0.75 * distanceScore) + (0.25 * freeRatio);
            case NORMAL_BALANCED -> (0.70 * proximityToTarget(fillRatio, 0.85)) + (0.30 * freeRatio);
            case EVENING_PEAK -> (0.60 * distanceScore) + (0.40 * freeRatio);
            default -> 1.0;
        };
    }

    private double zoneDistance(ParkingZone zone) {
        if (zone == null || zone.getSlots() == null || zone.getSlots().isEmpty()) {
            return Double.MAX_VALUE;
        }
        return zone.getSlots().stream()
                .map(com.parking.system.entity.ParkingSlot::getDistanceFromGate)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(Double.MAX_VALUE);
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
