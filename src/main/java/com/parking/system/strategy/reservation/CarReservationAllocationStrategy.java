package com.parking.system.strategy.reservation;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CarReservationAllocationStrategy implements ReservationAllocationStrategy {

    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    @Override
    public VehicleType supports() {
        return VehicleType.CAR;
    }

    @Override
    @Transactional
    public ReservationAllocationResult allocate(CreateReservationRequest request) {
        if (request.getZoneId() == null || request.getSlotId() == null) {
            throw new BusinessException("Car reservation requires zoneId and slotId");
        }

        ParkingZone zone = parkingZoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new BusinessException("Parking zone not found"));
        if (zone.getVehicleType() != VehicleType.CAR) {
            throw new BusinessException("Selected zone is not a car zone");
        }

        ParkingSlot slot = parkingSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new BusinessException("Parking slot not found"));
        if (!slot.getZone().getId().equals(zone.getId())) {
            throw new BusinessException("Parking slot does not belong to selected zone");
        }
        if (!slot.isAvailable()) {
            throw new BusinessException("Parking slot is not available");
        }

        slot.reserve();
        ParkingSlot savedSlot = parkingSlotRepository.save(slot);
        return ReservationAllocationResult.builder()
                .assignedZone(zone)
                .assignedSlot(savedSlot)
                .build();
    }
}
