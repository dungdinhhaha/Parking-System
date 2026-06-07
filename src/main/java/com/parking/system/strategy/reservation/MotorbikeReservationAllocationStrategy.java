package com.parking.system.strategy.reservation;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MotorbikeReservationAllocationStrategy implements ReservationAllocationStrategy {

    private final ParkingZoneRepository parkingZoneRepository;

    @Override
    public VehicleType supports() {
        return VehicleType.MOTORBIKE;
    }

    @Override
    @Transactional
    public ReservationAllocationResult allocate(CreateReservationRequest request) {
        if (request.getZoneId() == null) {
            throw new BusinessException("Motorbike reservation requires zoneId");
        }

        ParkingZone zone = parkingZoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new BusinessException("Parking zone not found"));
        if (zone.getVehicleType() != VehicleType.MOTORBIKE) {
            throw new BusinessException("Selected zone is not a motorbike zone");
        }
        if (!zone.hasAvailableCapacity()) {
            throw new BusinessException("Parking zone is full");
        }

        zone.increaseReservedCount();
        ParkingZone savedZone = parkingZoneRepository.save(zone);
        return ReservationAllocationResult.builder()
                .assignedZone(savedZone)
                .build();
    }
}
