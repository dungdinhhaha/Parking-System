package com.parking.system.service.reservation;

import com.parking.system.dto.response.ParkingSlotResponse;
import com.parking.system.dto.response.ParkingZoneResponse;
import com.parking.system.dto.response.ReservationAvailabilityResponse;
import com.parking.system.entity.ParkingBuilding;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingBuildingRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationAvailabilityQueryService {

    private final ParkingBuildingRepository parkingBuildingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final ReservationMapper reservationMapper;

    @Transactional(readOnly = true)
    public ReservationAvailabilityResponse getAvailability(String buildingCode) {
        ParkingBuilding building = parkingBuildingRepository.findByBuildingCodeIgnoreCase(buildingCode)
                .orElseThrow(() -> new BusinessException("Parking building not found"));

        List<ParkingSlotResponse> availableCarSlots = parkingSlotRepository
                .findAllByZone_Floor_Building_BuildingCodeIgnoreCaseAndStatus(building.getBuildingCode(), SlotStatus.AVAILABLE)
                .stream()
                .filter(slot -> slot.getVehicleType() == VehicleType.CAR)
                .sorted(Comparator.comparing(ParkingSlot::getDistanceFromGate, Comparator.nullsLast(Double::compareTo)))
                .map(reservationMapper::toSlotResponse)
                .toList();

        List<ParkingZoneResponse> availableMotorbikeZones = parkingZoneRepository
                .findAllByFloor_Building_BuildingCodeIgnoreCaseAndVehicleTypeAndStatus(building.getBuildingCode(), VehicleType.MOTORBIKE, ZoneStatus.AVAILABLE)
                .stream()
                .filter(ParkingZone::hasAvailableCapacity)
                .sorted(Comparator.comparing(ParkingZone::getAvailableCapacity).reversed())
                .map(reservationMapper::toZoneResponse)
                .toList();

        return reservationMapper.toAvailabilityResponse(building, availableCarSlots, availableMotorbikeZones);
    }
}
// Edited by Codex
