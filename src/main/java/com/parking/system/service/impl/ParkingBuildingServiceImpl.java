package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateParkingBuildingRequest;
import com.parking.system.dto.request.UpdateParkingBuildingRequest;
import com.parking.system.dto.response.ParkingBuildingResponse;
import com.parking.system.entity.ParkingBuilding;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingBuildingRepository;
import com.parking.system.repository.ParkingFloorRepository;
import com.parking.system.service.ParkingBuildingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingBuildingServiceImpl implements ParkingBuildingService {

    private final ParkingBuildingRepository parkingBuildingRepository;
    private final ParkingFloorRepository parkingFloorRepository;

    @Override
    @Transactional
    public ParkingBuildingResponse create(CreateParkingBuildingRequest request) {
        if (parkingBuildingRepository.existsByBuildingCodeIgnoreCase(request.getBuildingCode())) {
            throw new BusinessException("Parking building already exists");
        }
        ParkingBuilding building = new ParkingBuilding();
        apply(building, request.getBuildingCode(), request.getName(), request.getAddress(), request.getOpenTime(), request.getCloseTime(), request.getStatus(), request.getDescription());
        return toResponse(parkingBuildingRepository.save(building));
    }

    @Override
    @Transactional
    public ParkingBuildingResponse update(Long id, UpdateParkingBuildingRequest request) {
        ParkingBuilding building = getEntity(id);
        parkingBuildingRepository.findByBuildingCodeIgnoreCase(request.getBuildingCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Parking building already exists");
                });
        apply(building, request.getBuildingCode(), request.getName(), request.getAddress(), request.getOpenTime(), request.getCloseTime(), request.getStatus(), request.getDescription());
        return toResponse(parkingBuildingRepository.save(building));
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingBuildingResponse get(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingBuildingResponse> getAll() {
        return parkingBuildingRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParkingBuilding building = getEntity(id);
        if (parkingFloorRepository.existsByBuilding_Id(building.getId())) {
            throw new BusinessException("Cannot delete building while floors still exist");
        }
        parkingBuildingRepository.delete(building);
    }

    private ParkingBuilding getEntity(Long id) {
        return parkingBuildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Parking building not found"));
    }

    private void apply(ParkingBuilding building,
                       String buildingCode,
                       String name,
                       String address,
                       java.time.LocalTime openTime,
                       java.time.LocalTime closeTime,
                       com.parking.system.enums.BuildingStatus status,
                       String description) {
        building.setBuildingCode(buildingCode);
        building.setName(name);
        building.setAddress(address);
        building.setOpenTime(openTime);
        building.setCloseTime(closeTime);
        building.setStatus(status != null ? status : com.parking.system.enums.BuildingStatus.OPEN);
        building.setDescription(description);
    }

    private ParkingBuildingResponse toResponse(ParkingBuilding building) {
        return ParkingBuildingResponse.builder()
                .id(building.getId())
                .buildingCode(building.getBuildingCode())
                .name(building.getName())
                .address(building.getAddress())
                .openTime(building.getOpenTime())
                .closeTime(building.getCloseTime())
                .status(building.getStatus())
                .description(building.getDescription())
                .floorCount((long) building.getFloors().size())
                .createdAt(building.getCreatedAt())
                .updatedAt(building.getUpdatedAt())
                .build();
    }
}
