package com.parking.system.strategy.allocation;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ReservationRepository;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CarGreedySlotAllocationStrategy implements ParkingAllocationStrategy {

    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingZoneRepository parkingZoneRepository;

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

        ParkingSlot slot = parkingSlotRepository
                .findAllByZone_Floor_Building_BuildingCodeIgnoreCaseAndStatus(request.getBuildingCode(), SlotStatus.AVAILABLE)
                .stream()
                .filter(candidate -> candidate.getVehicleType() == VehicleType.CAR)
                .sorted(Comparator.comparing(ParkingSlot::getDistanceFromGate, Comparator.nullsLast(Double::compareTo)))
                .findFirst()
                .orElseThrow(() -> new BusinessException("No available car slot"));

        slot.occupy();
        ParkingSlot savedSlot = parkingSlotRepository.save(slot);
        return AllocationResult.builder()
                .assignedZone(savedSlot.getZone())
                .assignedSlot(savedSlot)
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
                .filter(reservation -> reservation.getAssignedSlot() != null)
                .filter(reservation -> reservation.getAssignedSlot().getZone().getFloor().getBuilding().getBuildingCode()
                        .equalsIgnoreCase(request.getBuildingCode()))
                .orElse(null);
    }

    private AllocationResult allocateFromReservation(AllocationRequest request, Reservation reservation) {
        ParkingSlot slot = reservation.getAssignedSlot();
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
}
