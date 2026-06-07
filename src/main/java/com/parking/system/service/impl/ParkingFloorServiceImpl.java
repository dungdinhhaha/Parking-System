package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateParkingFloorRequest;
import com.parking.system.dto.request.UpdateParkingFloorRequest;
import com.parking.system.dto.response.ParkingFloorResponse;
import com.parking.system.entity.ParkingFloor;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingFloorRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ParkingBuildingRepository;
import com.parking.system.service.ParkingFloorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingFloorServiceImpl implements ParkingFloorService {

    private final ParkingFloorRepository parkingFloorRepository;
    private final ParkingBuildingRepository parkingBuildingRepository;
    private final ParkingZoneRepository parkingZoneRepository;

    @Override
    @Transactional
    public ParkingFloorResponse create(CreateParkingFloorRequest request) {
        if (parkingFloorRepository.findByBuilding_IdAndFloorCodeIgnoreCase(request.getBuildingId(), request.getFloorCode()).isPresent()) {
            throw new BusinessException("Parking floor already exists in this building");
        }
        ParkingFloor floor = new ParkingFloor();
        floor.setBuilding(parkingBuildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new BusinessException("Parking building not found")));
        apply(floor, request.getFloorCode(), request.getName(), request.getDescription());
        return toResponse(parkingFloorRepository.save(floor));
    }

    @Override
    @Transactional
    public ParkingFloorResponse update(Long id, UpdateParkingFloorRequest request) {
        ParkingFloor floor = getEntity(id);
        parkingFloorRepository.findByBuilding_IdAndFloorCodeIgnoreCase(floor.getBuilding().getId(), request.getFloorCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Parking floor already exists in this building");
                });
        apply(floor, request.getFloorCode(), request.getName(), request.getDescription());
        return toResponse(parkingFloorRepository.save(floor));
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingFloorResponse get(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingFloorResponse> getAll() {
        return parkingFloorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParkingFloor floor = getEntity(id);
        if (parkingZoneRepository.existsByFloor_Id(floor.getId())) {
            throw new BusinessException("Cannot delete floor while zones still exist");
        }
        parkingFloorRepository.delete(floor);
    }

    private ParkingFloor getEntity(Long id) {
        return parkingFloorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Parking floor not found"));
    }

    private void apply(ParkingFloor floor, String floorCode, String name, String description) {
        floor.setFloorCode(floorCode);
        floor.setName(name);
        floor.setDescription(description);
    }

    private ParkingFloorResponse toResponse(ParkingFloor floor) {
        return ParkingFloorResponse.builder()
                .id(floor.getId())
                .buildingId(floor.getBuilding().getId())
                .buildingName(floor.getBuilding().getName())
                .floorCode(floor.getFloorCode())
                .name(floor.getName())
                .description(floor.getDescription())
                .zoneCount((long) floor.getZones().size())
                .createdAt(floor.getCreatedAt())
                .updatedAt(floor.getUpdatedAt())
                .build();
    }
}
