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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MotorbikeGreedyZoneAllocationStrategy implements ParkingAllocationStrategy {

    private final ReservationRepository reservationRepository;
    private final ParkingZoneRepository parkingZoneRepository;

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

        ParkingZone zone = parkingZoneRepository
                .findAllByFloor_Building_BuildingCodeIgnoreCaseAndVehicleTypeAndStatus(
                        request.getBuildingCode(),
                        VehicleType.MOTORBIKE,
                        ZoneStatus.AVAILABLE)
                .stream()
                .filter(ParkingZone::hasAvailableCapacity)
                .sorted(Comparator.comparing(ParkingZone::getAvailableCapacity).reversed())
                .findFirst()
                .orElseThrow(() -> new BusinessException("No available motorbike zone"));

        zone.increaseCurrentCount();
        ParkingZone savedZone = parkingZoneRepository.save(zone);
        return AllocationResult.builder()
                .assignedZone(savedZone)
                .source(AllocationSource.GREEDY)
                .build();
    }

    private Reservation findReservation(AllocationRequest request) {
        return reservationRepository
                .findFirstByPlateNumberIgnoreCaseAndVehicleTypeAndStatusAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByCreatedAtDesc(
                        request.getPlateNumber(),
                        request.getVehicleType(),
                        ReservationStatus.CONFIRMED,
                        request.getAllocationTime(),
                        request.getAllocationTime())
                .filter(reservation -> reservation.getAssignedZone() != null)
                .filter(reservation -> reservation.getAssignedZone().getFloor().getBuilding().getBuildingCode()
                        .equalsIgnoreCase(request.getBuildingCode()))
                .orElse(null);
    }

    private AllocationResult allocateFromReservation(AllocationRequest request, Reservation reservation) {
        ParkingZone zone = reservation.getAssignedZone();
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
}
