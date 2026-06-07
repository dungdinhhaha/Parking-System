package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateParkingZoneRequest;
import com.parking.system.dto.request.UpdateParkingZoneRequest;
import com.parking.system.dto.response.ParkingZoneResponse;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.ZoneStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingFloorRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.service.ParkingZoneService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingZoneServiceImpl implements ParkingZoneService {

    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingFloorRepository parkingFloorRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    @Override
    @Transactional
    public ParkingZoneResponse create(CreateParkingZoneRequest request) {
        if (parkingZoneRepository.findByFloor_IdAndZoneCodeIgnoreCase(request.getFloorId(), request.getZoneCode()).isPresent()) {
            throw new BusinessException("Parking zone already exists in this floor");
        }
        ParkingZone zone = new ParkingZone();
        zone.setFloor(parkingFloorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new BusinessException("Parking floor not found")));
        apply(zone, request.getZoneCode(), request.getName(), request.getVehicleType(), request.getCapacity(), request.getStatus());
        return toResponse(parkingZoneRepository.save(zone));
    }

    @Override
    @Transactional
    public ParkingZoneResponse update(Long id, UpdateParkingZoneRequest request) {
        ParkingZone zone = getEntity(id);
        parkingZoneRepository.findByFloor_IdAndZoneCodeIgnoreCase(zone.getFloor().getId(), request.getZoneCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Parking zone already exists in this floor");
                });
        apply(zone, request.getZoneCode(), request.getName(), request.getVehicleType(), request.getCapacity(), request.getStatus());
        return toResponse(parkingZoneRepository.save(zone));
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingZoneResponse get(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingZoneResponse> getAll() {
        return parkingZoneRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParkingZone zone = getEntity(id);
        if (parkingSlotRepository.existsByZone_Id(zone.getId())) {
            throw new BusinessException("Cannot delete zone while slots still exist");
        }
        parkingZoneRepository.delete(zone);
    }

    private ParkingZone getEntity(Long id) {
        return parkingZoneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Parking zone not found"));
    }

    private void apply(ParkingZone zone,
                       String zoneCode,
                       String name,
                       com.parking.system.enums.VehicleType vehicleType,
                       Integer capacity,
                       ZoneStatus status) {
        zone.setZoneCode(zoneCode);
        zone.setName(name);
        zone.setVehicleType(vehicleType);
        zone.setCapacity(capacity);
        zone.setStatus(status != null ? status : ZoneStatus.AVAILABLE);
    }

    private ParkingZoneResponse toResponse(ParkingZone zone) {
        return ParkingZoneResponse.builder()
                .id(zone.getId())
                .floorId(zone.getFloor().getId())
                .floorCode(zone.getFloor().getFloorCode())
                .zoneCode(zone.getZoneCode())
                .name(zone.getName())
                .vehicleType(zone.getVehicleType())
                .capacity(zone.getCapacity())
                .currentCount(zone.getCurrentCount())
                .reservedCount(zone.getReservedCount())
                .availableCapacity(zone.getAvailableCapacity())
                .status(zone.getStatus())
                .slotCount((long) zone.getSlots().size())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }
}
